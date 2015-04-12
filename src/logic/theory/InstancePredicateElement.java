package logic.theory;

import basic.StringUtil;
import vector.Vector;
import logic.expression.formula.Predicate;
import logic.expression.formula.PredicateInstance;

/**
 * Created by samuelkolb on 11/11/14.
 *
 * @author Samuel Kolb
 */
public class InstancePredicateElement extends Structure.PredicateElement {

	//region Variables
	private final Vector<PredicateInstance> instances;
	//endregion

	//region Construction
	public InstancePredicateElement(Predicate predicate) {
		this(predicate, new Vector<>());
	}

	public InstancePredicateElement(Predicate predicate, Vector<PredicateInstance> instances) {
		super(predicate);
		this.instances = instances;
		for(PredicateInstance instance : instances)
			if(!getPredicate().equals(instance.getPredicate()))
				throw new IllegalArgumentException("Incorrect instance predicate");
	}

	public InstancePredicateElement(PredicateInstance... instances) {
		super(instances[0].getPredicate());
		for(int i = 1; i < instances.length; i++)
			if(!getPredicate().equals(instances[i].getPredicate()))
				throw new IllegalArgumentException("Inconsistent instance predicates");
		this.instances = new Vector<>(instances);
	}
	//endregion

	//region Public methods

	@Override
	public String print() {
		String name = getPredicate().getName();
		String[] tuples = new String[instances.size()];
		for(int i = 0; i < instances.size(); i++)
			tuples[i] = printInstance(instances.get(i));
		return name + " = {" + StringUtil.join("; ", tuples) + "}";
	}

	//endregion

	private String printInstance(PredicateInstance instance) {
		return "(" + StringUtil.join(", ", instance.getTerms().getArray()) + ")";
	}
}
