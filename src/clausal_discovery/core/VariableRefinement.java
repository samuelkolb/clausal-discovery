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

	private final Set<ValidatedClause> resultSet = Collections.newSetFromMap(new ConcurrentHashMap<>());

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
	 * @param list	    	The instance list
	 * @param background	The background theories provided to the search
	 * @param validityTest	The validity test
	 */
	public VariableRefinement(LogicBase logicBase, InstanceList list, Vector<Theory> background,
							  Predicate<ValidatedClause> validityTest) {
		this.backgroundTheories = background.grow(new InlineTheory(logicBase.getSymmetryFormulas()));
		this.logicBase = logicBase;
		this.executor = IdpExecutor.get();
		this.instanceList = list;
		this.validityCalculator = new ParallelValidityCalculator(getLogicBase(), executor, background);
		this.validityAcceptance = validityTest;
		Log.LOG.printLine("Instance list with " + getInstanceList().size() + " elements\n");
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

	}

	@Override
	public boolean nodeSelected(Node<ValidatedClause> node) {
		return /**true/*/!subsetOccurs(node.getValue(), true)/**/;
	}

	@Override
	public void nodeProcessed(Node<ValidatedClause> node, Result<ValidatedClause> result) {

	}

	@Override
	public void nodeExpanded(Node<ValidatedClause> node, List<Node<ValidatedClause>> childNodes) {

	}

	@Override
	public boolean processSolution(Result<ValidatedClause> result, Node<ValidatedClause> node) {
		if(/**/subsetOccurs(node.getValue(), false) || /**/!this.validityAcceptance.test(node.getValue()))
			return true;
		new EntailmentTestRunnable(result, node).run();
		resultSet.add(node.getValue());
		return !node.getValue().coversAll();
	}

	@Override
	public void searchComplete(Result<ValidatedClause> result) {
		validityCalculator.shutdown();
		getExcessTimer().start();
		prune(result);
		getExcessTimer().pause();
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
				.filter(c -> canPrune(c, clause))
				.map(ValidatedClause::getClause).map(this::getClause)
				.collect(Collectors.toList());
		//formulas.addAll(getLogicBase().getSymmetryFormulas());
		Vector<Theory> theories = new Vector<>(new InlineTheory(formulas));
		//Vector<Theory> background = getBackgroundTheories().grow(new InlineTheory(getLogicBase().getSymmetryFormulas()));
		return new KnowledgeBase(logicBase.getVocabulary(), theories, getBackgroundTheories(), new Vector<>());
	}

	protected Clause getClause(StatusClause clause) {
		return new StatusClauseConverter().apply(clause);
	}

	private List<ValidatedClause> getChildren(ValidatedClause validatedClause) {
		StatusClause clause = validatedClause.getClause();
		List<StatusClause> children = new ArrayList<>();
		for(int i = clause.getIndex() + 1; i < getInstanceList().size(); i++)
			clause.processIfRepresentative(getInstanceList().getInstance(i, !clause.hasHead())).ifPresent(children::add);
		if(!clause.hasHead())
			for(int i = 0; i < getInstanceList().size(); i++)
				clause.processIfRepresentative(getInstanceList().getInstance(i, false)).ifPresent(children::add);
		return children.stream().map(validityCalculator::getValidatedClause).collect(Collectors.toList());
	}

	private boolean canPrune(ValidatedClause clause, ValidatedClause newClause) {
		return /**/clause.coversAll()
				|| /**/canPruneSoft(clause, newClause);
	}

	private boolean canPruneSoft(ValidatedClause clause, ValidatedClause newClause) {
		return clause.getSupportCount() == newClause.getSupportCount()
				&& clause.getValidity().equals(newClause.getValidity());
	}

	private boolean subsetOccurs(ValidatedClause statusClause, boolean pruneHard) {
		for(ValidatedClause resultClause : resultSet)
			if((pruneHard ? resultClause.coversAll() : canPruneSoft(resultClause, statusClause))
					&& resultClause.getClause().isSubsetOf(statusClause.getClause())) {
				Log.LOG.formatLine("%s   %s (%s)", pruneHard ? "FILTER" : "REJECT", statusClause, resultClause);
				return true;
			}
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
