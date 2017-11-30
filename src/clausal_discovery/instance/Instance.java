package clausal_discovery.instance;

import clausal_discovery.core.PredicateDefinition;
import logic.bias.Type;
import logic.expression.formula.Atom;
import logic.expression.formula.Predicate;
import logic.expression.term.Term;
import logic.expression.term.Variable;
import vector.SafeList;

import java.util.Map;

/**
 * An instance holds a predicate and variable indices, representing a predicate instance
 *
 * @author Samuel Kolb
 */
public class Instance {

	//region Variables
	private PredicateDefinition definition;

	public PredicateDefinition getDefinition() {
		return definition;
	}

	public Predicate getPredicate() {
		return getDefinition().getPredicate();
	}

	private SafeList<Integer> variableIndices;

	public SafeList<Integer> getVariableIndices() {
		return variableIndices;
	}

	private int max;

	public int getMax() {
		return max;
	}

	//endregion

	//region Construction

	/**
	 * Creates an instance
	 * @param definition        The predicate definition of this instance
	 * @param variableIndices   The variable indices
	 */
	public Instance(PredicateDefinition definition, SafeList<Integer> variableIndices) {
		assert definition.getPredicate().getArity() == variableIndices.size();
		this.definition = definition;
		this.variableIndices = definition.isSymmetric() ? variableIndices.sortedCopy() : variableIndices;
		this.max = variableIndices.get(0);
		for(int i = 1; i < variableIndices.size(); i++)
			this.max = Math.max(this.max, variableIndices.get(i));
	}

	//endregion

	//region Public methods

	@Override
	public String toString() {
		return getPredicate().getName() + variableIndices;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Instance instance = (Instance) o;
		return getPredicate().equals(instance.getPredicate()) && variableIndices.equals(instance.variableIndices);

	}

	@Override
	public int hashCode() {
		int result = getPredicate().hashCode();
		result = 31 * result + variableIndices.hashCode();
		return result;
	}

	/**
	 * Turns this instance into an atom
	 * @param variableMap	The variable map to convert variable indices to variables
	 * @return	An atom
	 */
	public Atom makeAtom(Map<Integer, Variable> variableMap) {
		Term[] terms = new Term[getVariableIndices().size()];
		for(int i = 0; i < getVariableIndices().size(); i++) {
			Integer integer = getVariableIndices().get(i);
			if(!variableMap.containsKey(integer))
				variableMap.put(integer, getVariable(i, integer));
			else if(variableMap.get(integer).getType().isSuperTypeOf(getPredicate().getTypes().get(i)))
				variableMap.get(integer).setType(getPredicate().getTypes().get(i));
			else if(!getPredicate().getTypes().get(i).isSuperTypeOf(variableMap.get(integer).getType()))
				throw new IllegalStateException();
			terms[i] = variableMap.get(integer);
		}
		return getPredicate().getInstance(terms);
	}

	private Variable getVariable(int i, Integer integer) {
		Type type = getPredicate().getTypes().get(i);
		return new Variable(getPredicate().getTypes().get(i).getName() + (integer + 1), type);
	}

	//endregion
}
