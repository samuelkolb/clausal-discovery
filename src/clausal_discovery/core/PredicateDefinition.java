package clausal_discovery.core;

import logic.bias.Type;
import logic.expression.formula.Formula;
import logic.expression.formula.Predicate;
import logic.expression.formula.PredicateInstance;
import logic.expression.term.Term;
import vector.SafeList;
import vector.Vector;

import java.util.Collections;
import java.util.List;

/**
 * A predicate definition holds attributes of a predicate.
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
		this(predicate, false);
	}

	/**
	 * Creates a new predicate definition
	 * @param predicate		The predicate
	 * @param calculated	If the predicate is not given but calculated based on a theory
	 */
	public PredicateDefinition(Predicate predicate, boolean calculated) {
		this.predicate = predicate;
		checkIfValid(predicate);
		this.calculated = calculated;
	}

	//endregion

	//region Public methods
	public int getArity() {
		return getPredicate().getArity();
	}

	public SafeList<Type> getTypes() {
		return getPredicate().getTypes();
	}

	/**
	 * Checks whether the given variables can be used to create an instance of this predicate definition.
	 * @param variables	The variable indices
	 * @return	True iff the given variables are valid
	 */
	public boolean accepts(SafeList<Integer> variables) {
		// Check if the number of given variables is correct
		if(variables.size() != getPredicate().getArity()) {
			return false;
		}
		// Check if the variables are all positive
		return variables.all(e -> e >= 0);
	}

	/**
	 * Transforms the input variables.
	 * @param variables	The input variables
	 * @return	The transformed input variables
	 */
	public SafeList<Integer> transform(SafeList<Integer> variables) {
		return variables;
	}

	/**
	 * Calculates the ground instances for this predicate definition, given a list of terms.
	 * @param terms	The terms
	 * @return	A list of ground instances
	 */
	public List<PredicateInstance> getGroundInstances(Term[] terms) {
		return Collections.singletonList(getPredicate().getInstance(terms));
	}

	/**
	 * Returns background knowledge about this definitions predicate.
	 * @return	A list of logical formulas.
	 */
	public List<Formula> getBackground() {
		return Collections.emptyList();
	}

	//endregion

	protected void checkIfValid(Predicate predicate) { }
}
