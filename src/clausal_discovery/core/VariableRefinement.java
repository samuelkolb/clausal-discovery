package clausal_discovery.core;

import clausal_discovery.instance.InstanceList;
import clausal_discovery.validity.ParallelValidityCalculator;
import clausal_discovery.validity.ValidatedClause;
import clausal_discovery.validity.ValidityCalculator;
import idp.IdpExecutor;
import log.Log;
import logic.expression.formula.Clause;
import logic.expression.formula.Formula;
import logic.theory.InlineTheory;
import logic.theory.KnowledgeBase;
import logic.theory.LogicExecutor;
import logic.theory.Theory;
import time.Stopwatch;
import vector.Vector;
import version3.algorithm.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The variable refinement class implements the ExpansionOperator and ResultPolicy for clausal discovery.
 * It also represents a plugin that has to be added to the search algorithm for its correct working.
 *
 * @author Samuel Kolb
 */
public class VariableRefinement implements ExpansionOperator<ValidatedClause>, ResultPolicy<ValidatedClause>,
		Plugin<ValidatedClause> {

	private class EntailmentTestRunnable implements Runnable {

		private final Result<ValidatedClause> result;

		private final Node<ValidatedClause> node;

		private EntailmentTestRunnable(Result<ValidatedClause> result, Node<ValidatedClause> node) {
			this.result = result;
			this.node = node;
		}

		@Override
		public void run() {
			Log.LOG.saveState()/*.off()/**/;
			if(!entails(result.getSolutions(), node.getValue())) {
				result.addNode(node);
				Log.LOG.print("NEW      ");
			} else {
				Log.LOG.print("DENIED   ");
			}
			Log.LOG.printLine(node.getValue()).revert();
		}
	}

	// region Variables

	// IVAR instanceList - The instance list used for clause generation

	private final InstanceList instanceList;

	public InstanceList getInstanceList() {
		return instanceList;
	}

	// IVAR logicBase - The logic base containing the search parameters

	private final LogicBase logicBase;

	public LogicBase getLogicBase() {
		return logicBase;
	}

	// IVAR executor - The logic executor used validity and entailment tests

	private final LogicExecutor executor;

	// IVAR backgroundTheories - The background theories provided to the search

	private final Vector<Theory> backgroundTheories;

	public Vector<Theory> getBackgroundTheories() {
		return backgroundTheories;
	}

	// IVAR validityCalculator - The validity calculator used for validity tests

	private final ValidityCalculator validityCalculator;

	// IVAR resultSet - The result set used for efficient subset tests

	private final Set<StatusClause> resultSet = Collections.newSetFromMap(new ConcurrentHashMap<>());

	// IVAR resultQueue - The result queue queues entailment calculations

	private final ExecutorService resultQueue;

	// IVAR excessTimer - A stopwatch that measures the excess time to finish entailment checks

	private final Stopwatch excessTimer = new Stopwatch();

	public Stopwatch getExcessTimer() {
		return excessTimer;
	}

	// IVAR validityAcceptance - Predicate to indicate what validated clauses are accepted as valid clauses

	private final Predicate<ValidatedClause> validityAcceptance;

	// endregion

	// region Construction

	/**
	 * Creates a new variable refinement operator
	 * @param logicBase    	The logic base holding the vocabulary and examples
	 * @param variables    	The number of variables that can be introduced
	 * @param background	The background theories provided to the search
	 * @param validityTest	The validity test
	 */
	public VariableRefinement(LogicBase logicBase, int variables, Vector<Theory> background,
							  Predicate<ValidatedClause> validityTest) {
		this.backgroundTheories = background;
		this.instanceList = new InstanceList(logicBase.getSearchPredicates(), variables);
		Log.LOG.printLine("Instance list with " + getInstanceList().size() + " elements\n");
		this.logicBase = logicBase;
		this.executor = IdpExecutor.get();
		this.validityCalculator = new ParallelValidityCalculator(getLogicBase(), executor, background);
		this.resultQueue = Executors.newSingleThreadExecutor();
		this.validityAcceptance = validityTest;
	}

	// endregion

	// region Public methods

	@Override
	public List<ValidatedClause> expandNode(Node<ValidatedClause> node) {
		if(node.shouldPruneChildren())
			return new ArrayList<>();
		return getChildren(node.getValue());
	}

	@Override
	public void initialise(List<Node<ValidatedClause>> initialNodes, Result<ValidatedClause> result) {
		result.addDelegate(new Result.Delegate<ValidatedClause>() {
			@Override
			public void processResultNodeAdded(Result<ValidatedClause> result, Node<ValidatedClause> node) {
				resultSet.add(node.getValue().getClause());
			}

			@Override
			public void processResultNodeRemoved(Result<ValidatedClause> result, Node<ValidatedClause> node) {

			}
		});
	}

	@Override
	public boolean nodeSelected(Node<ValidatedClause> node) {
		return !subsetOccurs(node.getValue().getClause());
	}

	@Override
	public void nodeProcessed(Node<ValidatedClause> node, Result<ValidatedClause> result) {

	}

	@Override
	public void nodeExpanded(Node<ValidatedClause> node, List<Node<ValidatedClause>> childNodes) {

	}

	@Override
	public boolean processSolution(Result<ValidatedClause> result, Node<ValidatedClause> node) {
		if(!this.validityAcceptance.test(node.getValue()))
			return true;
		new EntailmentTestRunnable(result, node).run();
		//resultQueue.execute(new EntailmentTestRunnable(result, node));
		return false;
	}

	@Override
	public void searchComplete(Result<ValidatedClause> result) {
		validityCalculator.shutdown();
		resultQueue.shutdown();
		getExcessTimer().start();
		try {
			resultQueue.awaitTermination(10, TimeUnit.DAYS);
			prune(result);
		} catch(InterruptedException e) {
			throw new IllegalStateException(e);
		} finally {
			getExcessTimer().pause();
		}
	}

	/**
	 * Returns whether the given set of clauses entails the given clause
	 * @param clauses	The set of clauses
	 * @param clause	The potentially entailed clause
	 * @return	True iff the set of clauses logically entails the given clause
	 */
	public boolean entails(List<ValidatedClause> clauses, ValidatedClause clause) {
		return executor.entails(getProgram(clauses, clause), new InlineTheory(getClause(clause.getClause())));
	}

	// endregion

	// region Private methods

	protected KnowledgeBase getProgram(List<ValidatedClause> clauses, ValidatedClause clause) {
		List<Formula> formulas = clauses.stream()
				.filter(c -> c.coversAll() || c.getValidity().equals(clause.getValidity())) // TODO Choose approach
				//.filter(ValidatedClause::coversAll)
				.map(ValidatedClause::getClause).map(this::getClause)
				.collect(Collectors.toList());
		formulas.addAll(getLogicBase().getSymmetryFormulas());
		Vector<Theory> theories = new Vector<>(new InlineTheory(formulas));
		return new KnowledgeBase(logicBase.getVocabulary(), theories, getBackgroundTheories(), new Vector<>());
	}

	protected Clause getClause(StatusClause clause) {
		return new StatusClauseConverter().apply(clause);
	}

	private List<ValidatedClause> getChildren(ValidatedClause validatedClause) {
		StatusClause clause = validatedClause.getClause();
		List<StatusClause> children = new ArrayList<>();
		for(int i = clause.getIndex() + 1; i < getInstanceList().size(); i++)
			clause.processIfRepresentative(getInstanceList().getInstance(i, clause.inBody())).ifPresent(children::add);
		if(clause.inBody())
			for(int i = 0; i < getInstanceList().size(); i++)
				clause.processIfRepresentative(getInstanceList().getInstance(i, false)).ifPresent(children::add);
		return children.stream().map(validityCalculator::getValidatedClause).collect(Collectors.toList());
	}

	private boolean subsetOccurs(StatusClause statusClause) {
		for(StatusClause resultClause : resultSet)
			if(resultClause.isSubsetOf(statusClause)) {
				Log.LOG.printLine("INFO (" + resultClause + ") subset of (" + statusClause + ")");
				return true;
			} else {
				Log.LOG.printLine("INFO (" + resultClause + ") not a subset of (" + statusClause + ")");
			}
		if(!resultSet.isEmpty())
			Log.LOG.printLine("INFO ");
		return false;
	}

	protected void prune(Result<ValidatedClause> result) {
		pruneOne(result, 0);
	}

	protected void pruneOne(Result<ValidatedClause> result, int index) {
		List<ValidatedClause> clauses = result.getSolutions();
		for(int i = index; i < result.getSolutionCount() - 1; i++) {
			List<ValidatedClause> list = new ArrayList<>(result.getSolutionCount() - 1);
			for(int j = 0; j < result.getSolutionCount(); j++)
				if(j != i)
					list.add(clauses.get(j));
			if(entails(list, clauses.get(i))) {
				Node<ValidatedClause> pruned = result.removeNode(i);
				Log.LOG.printLine("PRUNED   " + pruned);
				pruneOne(result, i);
				return;
			}
		}
	}

	// endregion
}
