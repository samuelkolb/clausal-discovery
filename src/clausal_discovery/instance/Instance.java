package clausal_discovery.instance;

import logic.expression.formula.Predicate;
import vector.Vector;

/**
 * An instance holds a predicate and variable indices, representing a predicate instance
 *
 * @author Samuel Kolb
 */
public class Instance {

	//region Variables
	private Predicate predicate;

	public Predicate getPredicate() {
		return predicate;
	}

	private Vector<Integer> variableIndices;

	public Vector<Integer> getVariableIndices() {
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
	 * @param predicate         The predicate of this instance
	 * @param variableIndices   The variable indices
	 */
	public Instance(Predicate predicate, Vector<Integer> variableIndices) {
		assert predicate.getArity() == variableIndices.size();
		this.predicate = predicate;
		this.variableIndices = variableIndices;
		this.max = variableIndices.get(0);
		for(int i = 1; i < variableIndices.size(); i++)
			this.max = Math.max(this.max, variableIndices.get(i));
	}

	//endregion

	//region Public methods

	@Override
	public String toString() {
		return predicate.getName() + variableIndices.toString();
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Instance instance = (Instance) o;
		return predicate.equals(instance.predicate) && variableIndices.equals(instance.variableIndices);

	}

	@Override
	public int hashCode() {
		int result = predicate.hashCode();
		result = 31 * result + variableIndices.hashCode();
		return result;
	}

	//endregion
}
