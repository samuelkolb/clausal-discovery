package clausal_discovery;

import basic.ArrayUtil;
import idp.IdpExpressionPrinter;
import log.Log;
import logic.example.Example;
import logic.expression.formula.*;
import logic.expression.term.Term;
import logic.expression.term.Variable;
import logic.theory.LogicExecutor;
import logic.theory.LogicProgram;
import logic.theory.Structure;
import logic.theory.Theory;
import util.Numbers;
import util.Pair;
import vector.Vector;
import vector.WriteOnceVector;
import version3.algorithm.*;

import java.util.*;

/**
 * The variable refinement class implements the ExpansionOperator and ResultPolicy for clausal discovery.
 * It also represents a plugin that has to be added to the search algorithm for its correct working.
 *
 * @author Samuel Kolb
 */
public class VariableRefinement implements ExpansionOperator<StatusClause>, ResultPolicy<StatusClause>,
		Plugin<StatusClause> {

	private static final InfixPredicate INEQUALITY = new InfixPredicate("~=");

	private static class ClauseComparator implements Comparator<Numbers.Permutation> {
		@Override
		public int compare(Numbers.Permutation o1, Numbers.Permutation o2) {
			int index = 0;
			while(o1.getArray().length > index && o2.getArray().length > index) {
				if(o1.getArray()[index] > o2.getArray()[index])
					return 1;
				else if(o1.getArray()[index] < o2.getArray()[index])
					return -1;
				index++;
			}
			return Integer.compare(o1.getArray().length, o2.getArray().length);
		}
	}

	private final Vector<Instance> instances;

	private final LogicBase logicBase;

	public LogicBase getLogicBase() {
		return logicBase;
	}

	private final LogicExecutor executor;

	private final ValidityCalculator validityCalculator;

	private Set<StatusClause> resultSet = new HashSet<>();

	/**
	 * Creates a new variable refinement operator
	 * @param logicBase	The logic base holding the vocabulary and examples
	 * @param variables	The number of variables that can be introduced
	 * @param executor	The logic executor responsible for executing logical queries
	 */
	public VariableRefinement(LogicBase logicBase, int variables, LogicExecutor executor) {
		variables = getMaximalVariables(variables, logicBase.getVocabulary().getPredicates());
		List<Instance> instanceList = getInstances(logicBase.getVocabulary().getPredicates(), variables);
		this.instances = new Vector<>(instanceList.toArray(new Instance[instanceList.size()]));
		Log.LOG.printLine(this.instances.size() + " instances");
		this.logicBase = logicBase;
		this.executor = executor;
		this.validityCalculator = new ParallelValidityCalculator(getLogicBase(), executor);
	}

	private int getMaximalVariables(int variables, Vector<Predicate> predicates) {
		int max = 0;
		for(Predicate predicate : predicates)
			max = Math.max(max, predicate.getArity());
		return  max > 1 ? variables : 1;
	}

	@Override
	public List<StatusClause> expandNode(Node<StatusClause> node) {
		List<StatusClause> children = new ArrayList<>();
		StatusClause clause = node.getValue();

		addChildren(children, clause);
		if(clause.inBody())
			addChildren(children, clause.enterHead());

		return children;
	}

	private void addChildren(List<StatusClause> children, StatusClause clause) {
		for(int i = clause.getIndex() + 1; i < instances.size(); i++)
			if(clause.canProcess(i, instances.get(i)))
				children.add(clause.process(i, instances.get(i)));
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
		if(shouldBeInserted(result, node))
			result.addNode(node);
		return false;
	}

	private boolean isValid(Node<StatusClause> node) {
		Vector<Structure> structures = new WriteOnceVector<>(new Structure[getLogicBase().getExamples().size()]);
		for(Example example : getLogicBase().getExamples())
			structures.add(example.getStructure());
		return validityCalculator.isValid(getClause(node.getValue()));
	}

	/**
	 * Returns the Formula represented by the given status clause
	 * @param statusClause	The status clause
	 * @return	A logical Formula
	 */
	public Clause getClause(StatusClause statusClause) {
		return getClause(statusClause.getClauses());
	}

	Clause getClause(Vector<Pair<Integer, Boolean>> instances) {
		List<Atom> bodyAtoms = new ArrayList<>();
		List<Atom> headAtoms = new ArrayList<>();
		Map<Integer, Variable> variableMap = new HashMap<>();
		for(Pair<Integer, Boolean> pair : instances)
			(pair.getSecond() ? bodyAtoms : headAtoms).add(makeAtom(variableMap, pair.getFirst()));
		applyOI(variableMap.values(), bodyAtoms);
		return Clause.clause(bodyAtoms, headAtoms);
	}

	private Atom makeAtom(Map<Integer, Variable> variableMap, Integer index) {
		Instance instance = instances.get(index);
		Term[] terms = new Term[instance.getVariableIndices().size()];
		for(int i = 0; i < instance.getVariableIndices().size(); i++) {
			Integer integer = instance.getVariableIndices().get(i);
			if(!variableMap.containsKey(integer))
				variableMap.put(integer, getVariable(instance, i, integer));
			terms[i] = variableMap.get(integer);
		}
		return instance.getPredicate().getInstance(terms);
	}

	private Variable getVariable(Instance instance, int i, Integer integer) {
		return new Variable(instance.getPredicate().getTypes().get(i).getName() + (integer + 1));
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
			for(Clause subset : getSubsets(statusClause))
				if(getClause(resultClause).isSubsetOf(subset))
					return true;
		return false;
	}

	private List<Clause> getSubsets(StatusClause statusClause) {
		List<Clause> subsets = new ArrayList<>();
		for(int i = 0; i < statusClause.getClauses().size(); i++)
			subsets.add(getClause(new Vector<>(ArrayUtil.removeElement(statusClause.getClauses().getArray(), i))));
		return subsets;
	}

	private boolean shouldBeInserted(Result<StatusClause> result, Node<StatusClause> node) {
		List<Formula> formulas = new ArrayList<>();
		for(StatusClause statusClause : result.getSolutions())
			formulas.add(getClause(statusClause));
		Vector<Theory> theories = new WriteOnceVector<>(new Theory[1]);
		theories.add(new Theory(formulas));
		//Log.LOG.printLine("Does " + new IdpProgramPrinter().printTheory(new Theory(formulas), "T", "V") + " entail " + IdpExpressionPrinter.print(getClause(node.getValue())) + "?");
		LogicProgram logicProgram = new LogicProgram(logicBase.getVocabulary(), theories, new Vector<>());
		return !executor.entails(logicProgram, new Theory(getClause(node.getValue())));
	}

	private List<Instance> getInstances(Vector<Predicate> predicates, int variables) {
		Vector<InstanceSetPrototype> instanceSetPrototypes = InstanceSetPrototype.createInstanceSets(predicates);
		List<Instance> instanceList = new ArrayList<>();
		for(Numbers.Permutation choice : getChoices(variables, instanceSetPrototypes.length)) {
			InstanceSetPrototype instanceSetPrototype = instanceSetPrototypes.get(choice.getDistinctCount() - 1);
			instanceList.addAll(instanceSetPrototype.getInstances(choice.getArray()));
		}
		return instanceList;
	}

	private List<Numbers.Permutation> getChoices(int variables, int maxArity) {
		List<Numbers.Permutation> choices = new ArrayList<>();
		for(int i = 0; i < maxArity; i++)
			choices.addAll(Numbers.getChoices(variables, i + 1));
		choices.sort(new ClauseComparator());
		return choices;
	}

	/**
	 * Free any retained resources
	 */
	public void shutdown() {
		validityCalculator.shutdown();
	}
}
