package clausal_discovery.instance;

import clausal_discovery.core.PredicateDefinition;
import util.Numbers;
import vector.SafeList;
import logic.expression.formula.Predicate;

/**
 * The instance prototype describes a predicate and an ordering of its variables
 * e.g. p(0, 1, 2, 2)
 *
 * @author Samuel Kolb
 */
public class InstancePrototype {

	private PredicateDefinition definition;

	public PredicateDefinition getDefinition() {
		return definition;
	}

	private Predicate getPredicate() {
		return getDefinition().getPredicate();
	}

	private Numbers.Permutation permutation;

	private Numbers.Permutation getPermutation() {
		return permutation;
	}

	private int rank;

	public int getRank() {
		return rank;
	}

	/**
	 * Create a prototype instance
	 * @param definition    The associated predicate definition
	 * @param permutation   The permutation indicating the variable order
	 */
	public InstancePrototype(PredicateDefinition definition, Numbers.Permutation permutation) {
		assert permutation.getArray().length == definition.getPredicate().getArity();
		int max = permutation.getArray()[0];
		for(int i = 1; i < permutation.getArray().length; i++)
			max = Math.max(permutation.getArray()[i], max);
		this.definition = definition;
		this.permutation = permutation;
		this.rank = max + 1;
	}

	/**
	 * Creates an instance by assigning the indices to the variable order of this prototype
	 * @param indices   The indices that will substitute the variable numbers
	 * @return  An instance containing the given indices
	 */
	public Instance instantiate(int[] indices) {
		SafeList<Integer> variableIndices = new SafeList<>(getPermutation().substitute(indices).getIntegerArray());
		return new Instance(getDefinition(), variableIndices);
	}

	@Override
	public String toString() {
		return getPredicate().getName() + getPermutation();
	}
}
