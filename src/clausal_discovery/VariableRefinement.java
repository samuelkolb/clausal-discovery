package clausal_discovery;

import log.Log;
import logic.expression.formula.*;
import util.Numbers;
import util.Pair;
import vector.Vector;
import vector.WriteOnceVector;
import version3.algorithm.ExpansionOperator;
import version3.algorithm.Node;
import version3.algorithm.Result;
import version3.algorithm.ResultPolicy;
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
public class VariableRefinement implements ExpansionOperator<StatusClause>, ResultPolicy<StatusClause> {

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

	public VariableRefinement(LogicBase logicBase, int variables, LogicExecutor executor) {
		List<Instance> instanceList = getInstances(logicBase.getVocabulary().getPredicates(), variables);
		this.instances = new Vector<>(instanceList.toArray(new Instance[instanceList.size()]));
		Log.LOG.printLine(this.instances.size() + " instances");
		this.logicBase = logicBase;
		this.executor = executor;
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
		Theory theory = new Theory(getClause(node.getValue()));
		LogicProgram program = new LogicProgram(getLogicBase().getVocabulary(), theory, structures);
		return executor.isValid(program);
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
		Theory theory = new Theory(formulas);
		LogicProgram logicProgram = new LogicProgram(logicBase.getVocabulary(), theory, new Vector<>());
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
