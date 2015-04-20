package clausal_discovery.core;

import clausal_discovery.instance.InstanceList;
import clausal_discovery.validity.ParallelValidityCalculator;
import clausal_discovery.validity.ValidityCalculator;
import log.Log;
import logic.example.Example;
import logic.expression.formula.Clause;
import logic.expression.formula.Formula;
import logic.expression.formula.InfixPredicate;
import logic.theory.*;
import time.Stopwatch;
import vector.Vector;
import vector.WriteOnceVector;
import version3.algorithm.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The variable refinement class implements the ExpansionOperator and ResultPolicy for clausal discovery.
 * It also represents a plugin that has to be added to the search algorithm for its correct working.
 *
 * @author Samuel Kolb
 */
public class VariableRefinement implements ExpansionOperator<StatusClause>, ResultPolicy<StatusClause>,
		Plugin<StatusClause> {

	private class EntailmentTestRunnable implements Runnable {

		private final Result<StatusClause> result;

		private final Node<StatusClause> node;

		private EntailmentTestRunnable(Result<StatusClause> result, Node<StatusClause> node) {
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

	public static final InfixPredicate INEQUALITY = new InfixPredicate("~=");

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

	// endregion

	// region Construction

	/**
	 * Creates a new variable refinement operator
	 * @param logicBase	The logic base holding the vocabulary and examples
	 * @param variables	The number of variables that can be introduced
	 * @param executor	The logic executor responsible for executing logical queries
	 */
	public VariableRefinement(LogicBase logicBase, int variables, LogicExecutor executor) {
		this.instanceList = new InstanceList(logicBase.getSearchPredicates(), variables);
		Log.LOG.printLine("Instance list with " + getInstanceList().size() + " elements\n").printLine(getInstanceList()).newLine();
		this.logicBase = logicBase;
		this.executor = executor;
		this.validityCalculator = new ParallelValidityCalculator(getLogicBase(), executor);
		this.resultQueue = Executors.newSingleThreadExecutor();
	}

	// endregion

	// region Public methods

	@Override
	public List<StatusClause> expandNode(Node<StatusClause> node) {
		if(node.shouldPruneChildren())
			return new ArrayList<>();
		return getChildren(node.getValue());
	}

	@Override
	public void initialise(List<Node<StatusClause>> initialNodes, Result<StatusClause> result) {
		for(Node<StatusClause> node : initialNodes)
			validityCalculator.submitFormula(getClause(node.getValue()));
		result.addDelegate(new Result.Delegate<StatusClause>() {
			@Override
			public void processResultNodeAdded(Result<StatusClause> result, Node<StatusClause> node) {
				resultSet.add(node.getValue());
			}

			@Override
			public void processResultNodeRemoved(Result<StatusClause> result, Node<StatusClause> node) {

			}
		});
	}

	@Override
	public boolean nodeSelected(Node<StatusClause> node) {
		return !subsetOccurs(node.getValue());
	}

	@Override
	public void nodeProcessed(Node<StatusClause> node, Result<StatusClause> result) {

	}

	@Override
	public void nodeExpanded(Node<StatusClause> node, List<Node<StatusClause>> childNodes) {
		for(Node<StatusClause> childNode : childNodes)
			validityCalculator.submitFormula(getClause(childNode.getValue()));
	}

	@Override
	public boolean processSolution(Result<StatusClause> result, Node<StatusClause> node) {
		if(!isValid(node))
			return true;
		new EntailmentTestRunnable(result, node).run();
		//resultQueue.execute(new EntailmentTestRunnable(result, node));
		return false;
	}

	@Override
	public void searchComplete(Result<StatusClause> result) {
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
	public boolean entails(List<StatusClause> clauses, StatusClause clause) {
		return executor.entails(getProgram(clauses), new InlineTheory(getClause(clause)));
	}

	// endregion

	// region Private methods

	protected LogicProgram getProgram(List<StatusClause> clauses) {
		List<Formula> formulas = clauses.stream().map(this::getClause).collect(Collectors.toList());
		formulas.addAll(getLogicBase().getSymmetryFormulas());
		Vector<Theory> theories = new Vector<Theory>(new InlineTheory(formulas));
		return new LogicProgram(logicBase.getVocabulary(), theories, new Vector<>());
	}

	protected Clause getClause(StatusClause clause) {
		return new StatusClauseConverter().apply(clause);
	}

	private List<StatusClause> getChildren(StatusClause clause) {
		List<StatusClause> children = new ArrayList<>();
		for(int i = clause.getIndex() + 1; i < getInstanceList().size(); i++)
			clause.processIfRepresentative(getInstanceList().getInstance(i, clause.inBody())).ifPresent(children::add);
		if(clause.inBody())
			for(int i = 0; i < getInstanceList().size(); i++)
				clause.processIfRepresentative(getInstanceList().getInstance(i, false)).ifPresent(children::add);
		return children;
	}

	private boolean isValid(Node<StatusClause> node) {
		Vector<Structure> structures = new WriteOnceVector<>(new Structure[getLogicBase().getExamples().size()]);
		for(Example example : getLogicBase().getExamples())
			structures.add(example.getStructure());
		return validityCalculator.isValid(getClause(node.getValue()));
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

	protected void prune(Result<StatusClause> result) {
		pruneOne(result, 0);
	}

	protected void pruneOne(Result<StatusClause> result, int index) {
		List<StatusClause> clauses = result.getSolutions();
		for(int i = index; i < result.getSolutionCount() - 1; i++) {
			List<StatusClause> list = new ArrayList<>(result.getSolutionCount() - 1);
			for(int j = 0; j < result.getSolutionCount(); j++)
				if(j != i)
					list.add(clauses.get(j));
			if(entails(list, clauses.get(i))) {
				Node<StatusClause> pruned = result.removeNode(i);
				Log.LOG.printLine("PRUNED   " + pruned);
				pruneOne(result, i);
				return;
			}
		}
	}

	// endregion
}
