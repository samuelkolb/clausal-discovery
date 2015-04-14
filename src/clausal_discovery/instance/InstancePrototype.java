package clausal_discovery.instance;

import util.Numbers;
import vector.Vector;
import logic.expression.formula.Predicate;

/**
 * The instance prototype describes a predicate and an ordering of its variables
 * e.g. p(0, 1, 2, 2)
 *
 * @author Samuel Kolb
 */
public class InstancePrototype {

	private Predicate predicate;

	public Predicate getPredicate() {
		return predicate;
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
	 * @param predicate     The associated predicate
	 * @param permutation   The permutation indicating the variable order
	 */
	public InstancePrototype(Predicate predicate, Numbers.Permutation permutation) {
		assert permutation.getArray().length == predicate.getArity();
		int max = permutation.getArray()[0];
		for(int i = 1; i < permutation.getArray().length; i++)
			max = Math.max(permutation.getArray()[i], max);
		this.predicate = predicate;
		this.permutation = permutation;
		this.rank = max + 1;
	}

	/**
	 * Creates an instance by assigning the indices to the variable order of this prototype
	 * @param indices   The indices that will substitute the variable numbers
	 * @return  An instance containing the given indices
	 */
	public Instance instantiate(int[] indices) {
		Vector<Integer> variableIndices = new Vector<>(getPermutation().substitute(indices).getIntegerArray());
		return new Instance(getPredicate(), variableIndices);
	}

	@Override
	public String toString() {
		return getPredicate().getName() + getPermutation();
	}
}
