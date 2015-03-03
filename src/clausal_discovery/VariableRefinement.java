package clausal_discovery;

import idp.IdpProgramPrinter;
import log.Log;
import logic.expression.formula.*;
import logic.expression.visitor.ExpressionLogicPrinter;
import util.Numbers;
import util.Pair;
import vector.Vector;
import vector.WriteOnceVector;
import version3.algorithm.*;
import logic.example.Example;
import logic.expression.term.Term;
import logic.expression.term.Variable;
import logic.theory.LogicExecutor;
import logic.theory.LogicProgram;
import logic.theory.Structure;
import logic.theory.Theory;

import java.util.*;

/**
 * The variable refinement class implements the ExpansionOperator and ResultPolicy for clausal discovery
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

	public VariableRefinement(LogicBase logicBase, int variables, LogicExecutor executor) {
		List<Instance> instanceList = getInstances(logicBase.getVocabulary().getPredicates(), variables);
		this.instances = new Vector<>(instanceList.toArray(new Instance[instanceList.size()]));
		Log.LOG.printLine(this.instances.size() + " instances");
		this.logicBase = logicBase;
		this.executor = executor;
		this.validityCalculator = new ValidityCalculator(getLogicBase());
	}

	@Override
	public List<StatusClause> expandNode(Node<StatusClause> node) {
		List<StatusClause> children = new ArrayList<>();
		StatusClause clause = node.getValue();

		for(int i = clause.getIndex() + 1; i < instances.size(); i++)
			if(clause.canProcess(instances.get(i)))
				children.add(clause.process(i, instances.get(i)));

		if(clause.inBody()) {
			StatusClause headClause = clause.enterHead();
			for(int i = 0; i < instances.size(); i++)
				if(headClause.canProcess(instances.get(i)))
					children.add(headClause.process(i, instances.get(i)));
		}

		return children;
	}

	@Override
	public void initialise(List<Node<StatusClause>> initialNodes, Result<StatusClause> result) {
		for(Node<StatusClause> node : initialNodes)
			validityCalculator.submitFormula(getClause(node.getValue()));
	}

	@Override
	public boolean nodeSelected(Node<StatusClause> node) {
		return true;
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

	public Formula getClause(StatusClause value) {
		List<Atom> bodyAtoms = new ArrayList<>();
		List<Atom> headAtoms = new ArrayList<>();
		Map<Integer, Variable> variableMap = new HashMap<>();
		for(Pair<Integer, Boolean> pair : value.getClauses())
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
				variableMap.put(integer, new Variable("V" + integer));
			terms[i] = variableMap.get(integer);
		}
		return instance.getPredicate().getInstance(terms);
	}

	private void applyOI(Collection<Variable> variables, List<Atom> bodyAtoms) {
		Variable[] array = variables.toArray(new Variable[variables.size()]);
		for(int i = 0; i < array.length; i++)
			for(int j = i + 1; j < array.length; j++)
				if(array[i].getType().isSuperTypeOf(array[j].getType())
						|| array[j].getType().isSuperTypeOf(array[i].getType()))
					bodyAtoms.add(INEQUALITY.getInstance(array[i], array[j]));
	}

	private boolean shouldBeInserted(Result<StatusClause> result, Node<StatusClause> node) {
		List<Formula> formulas = new ArrayList<>();
		for(StatusClause statusClause : result.getSolutions())
			formulas.add(getClause(statusClause));
		Vector<Theory> theories = new WriteOnceVector<>(new Theory[1]);
		theories.add(new Theory(formulas));
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
}
