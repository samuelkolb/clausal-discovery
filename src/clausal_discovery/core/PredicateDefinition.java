package clausal_discovery.core;

import logic.bias.Type;
import logic.expression.formula.Predicate;
import vector.Vector;

/**
 * Created by samuelkolb on 14/04/15.
 *
 * @author Samuel Kolb
 */
public class PredicateDefinition {

	//region Variables

	// IVAR predicate - The predicate

	private final Predicate predicate;

	public Predicate getPredicate() {
		return predicate;
	}

	// IVAR symmetric - Whether or not the predicate is symmetric

	private final boolean symmetric;

	public boolean isSymmetric() {
		return symmetric;
	}

	// IVAR calculated - Whether or not the predicate is calculated based on other predicates

	private final boolean calculated;

	public boolean isCalculated() {
		return calculated;
	}

	//endregion

	//region Construction

	/**
	 * Creates a non-symmetric non-calculated predicate definition
	 * @param predicate	The predicate
	 */
	public PredicateDefinition(Predicate predicate) {
		this(predicate, false, false);
	}

	/**
	 * Creates a new predicate definition
	 * @param predicate		The predicate
	 * @param symmetric		If the predicate is symmetric
	 * @param calculated	If the predicate is not given but calculated based on a theory
	 */
	public PredicateDefinition(Predicate predicate, boolean symmetric, boolean calculated) {
		this.predicate = predicate;
		this.symmetric = symmetric;
		checkIfValid(predicate, symmetric);
		this.calculated = calculated;
	}

	//endregion

	//region Public methods
	public int getArity() {
		return getPredicate().getArity();
	}

	public Vector<Type> getTypes() {
		return getPredicate().getTypes();
	}
	//endregion

	private void checkIfValid(Predicate predicate, boolean symmetric) {
		if(symmetric && predicate.getArity() > 0) {
			Type type = predicate.getTypes().get(0);
			for(int i = 1; i < predicate.getArity(); i++)
				if(!type.equals(predicate.getTypes().get(i)))
					throw new IllegalArgumentException("In symmetric predicates all arguments must have the same type");
		}
	}


}
