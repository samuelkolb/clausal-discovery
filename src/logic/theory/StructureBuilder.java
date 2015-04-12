package logic.theory;

import build.Builder;
import vector.Vector;
import logic.bias.Type;
import logic.expression.formula.Predicate;
import logic.expression.formula.PredicateInstance;
import logic.expression.term.Constant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by samuelkolb on 11/11/14.
 *
 * @author Samuel Kolb
 */
public class StructureBuilder extends Builder<Structure> {

	//region Variables
	private final List<Structure.TypeElement> typeElements = new ArrayList<>();

	private final List<Structure.PredicateElement> predicateElements = new ArrayList<>();

	@SuppressWarnings("FieldCanBeLocal")
	private boolean isPositive = true;
	//endregion

	//region Construction

	//endregion

	//region Public methods

	public void addEmptyType(Type type) {
		addConstants(type);
	}

	public void addConstants(Constant... constants) {
		if(constants.length == 0)
			throw new IllegalArgumentException("Cannot infer type without constants");
		addConstants(constants[0].getType(), constants);
	}

	public void addConstants(Type type, Constant... constants) {
		typeElements.add(new ConstantTypeElement(type, new Vector<>(constants)));
	}

	public void addConstants(Type type, Collection<Constant> constants) {
		addConstants(type, constants.toArray(new Constant[constants.size()]));
	}

	public void addEmptyPredicate(Predicate predicate) {
		addPredicateInstances(predicate);
	}

	public void addPredicateInstances(PredicateInstance... instances) {
		if(instances.length == 0)
			throw new IllegalArgumentException("Cannot infer predicate without instances");
		addPredicateInstances(instances[0].getPredicate(), instances);
	}

	public void addPredicateInstances(Predicate predicate, PredicateInstance... instances) {
		predicateElements.add(new InstancePredicateElement(predicate, new Vector<>(instances)));
	}

	public void addPredicateInstances(Predicate predicate, Collection<PredicateInstance> instances) {
		addPredicateInstances(predicate, instances.toArray(new PredicateInstance[instances.size()]));
	}

	public void setPositive(boolean isPositive) {
		this.isPositive = isPositive;
	}

	/*
	 * Extracts all constants in the given predicate instances
	 * @param predicates	The given predicate instances
	 * @return	A vector of all different constant that occur at least once as a term of one of the given instances
	 *//*
	public static Vector<Constant> extractConstants(PredicateInstance... predicates) {
		Set<Constant> constants = new HashSet<>();
		for(PredicateInstance instance : predicates)
			for(Term term : instance.getTerms())
				if(term instanceof Constant)
					constants.add((Constant) term);
		return new Vector<>(constants.toArray(new Constant[constants.size()]));
	}/*/

	@Override
	public Structure sample() {
		return new Structure(getTypeElementVector(), getPredicateElementVector(), true);
	}

	@Override
	public void reset() {
		typeElements.clear();
		predicateElements.clear();
	}

	//endregion

	private Vector<Structure.PredicateElement> getPredicateElementVector() {
		return new Vector<>(predicateElements.toArray(new Structure.PredicateElement[predicateElements.size()]));
	}

	private Vector<Structure.TypeElement> getTypeElementVector() {
		return new Vector<>(typeElements.toArray(new Structure.TypeElement[typeElements.size()]));
	}
}
