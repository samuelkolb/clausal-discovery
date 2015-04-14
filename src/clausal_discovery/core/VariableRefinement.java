package clausal_discovery.core;

import clausal_discovery.instance.InstanceList;
import clausal_discovery.instance.PositionedInstance;
import clausal_discovery.validity.ParallelValidityCalculator;
import clausal_discovery.validity.ValidityCalculator;
import log.Log;
import logic.example.Example;
import logic.expression.formula.*;
import logic.expression.term.Variable;
import logic.theory.LogicExecutor;
import logic.theory.LogicProgram;
import logic.theory.Structure;
import logic.theory.Theory;
import time.Stopwatch;
import vector.Vector;
import vector.WriteOnceVector;
import version3.algorithm.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
			if(!executor.entails(getProgram(), new Theory(getClause(node.getValue())))) {
				result.addNode(node);
				Log.LOG.print("NEW      ");
			} else {
				Log.LOG.print("DENIED   ");
			}
			Log.LOG.printLine(node.getValue()).revert();
		}

		private LogicProgram getProgram() {
			List<Formula> formulas = new ArrayList<>();
			for(StatusClause statusClause : result.getSolutions())
				formulas.add(VariableRefinement.this.getClause(statusClause));

			formulas.addAll(getLogicBase().getSymmetryFormulas());
			Vector<Theory> theories = new WriteOnceVector<>(new Theory[1]);
			theories.add(new Theory(formulas));
			//Log.LOG.printLine("Does " + new IdpProgramPrinter().printTheory(new Theory(formulas), "T", "V") + " entail " + IdpExpressionPrinter.print(getClause(node.getValue())) + "?");
			return new LogicProgram(logicBase.getVocabulary(), theories, new Vector<>());
		}

	}

	private static final InfixPredicate INEQUALITY = new InfixPredicate("~=");

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
		Log.LOG.printLine("Instance list with " + getInstanceList().size() + " elements\n");
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
		} catch(InterruptedException e) {
			throw new IllegalStateException(e);
		} finally {
			getExcessTimer().pause();
		}
	}

	/**
	 * Returns the Formula represented by the given status clause
	 * @param statusClause	The status clause
	 * @return	A logical Formula
	 */
	public Clause getClause(StatusClause statusClause) {
		return getClause(statusClause.getInstances());
	}

	// endregion

	// region Private methods

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

	protected Clause getClause(Vector<PositionedInstance> instances) {
		List<Atom> bodyAtoms = new ArrayList<>();
		List<Atom> headAtoms = new ArrayList<>();
		Map<Integer, Variable> variableMap = new HashMap<>();
		for(PositionedInstance instance : instances)
			(instance.isInBody() ? bodyAtoms : headAtoms).add(instance.getInstance().makeAtom(variableMap));
		applyOI(variableMap.values(), bodyAtoms);
		return Clause.clause(bodyAtoms, headAtoms);
	}

	private void applyOI(Collection<Variable> variables, List<Atom> bodyAtoms) {
		Variable[] array = variables.toArray(new Variable[variables.size()]);
		for(int i = 0; i < array.length; i++)
			for(int j = i + 1; j < array.length; j++)
				if(array[i].getType().isSuperTypeOf(array[j].getType())
						|| array[j].getType().isSuperTypeOf(array[i].getType()))
					bodyAtoms.add(INEQUALITY.getInstance(array[i], array[j]));
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
	// endregion
}
