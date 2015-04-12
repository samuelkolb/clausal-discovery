package clausal_discovery.instance;

import logic.expression.formula.Predicate;
import util.Numbers;
import vector.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuelkolb on 12/04/15.
 *
 * @author Samuel Kolb
 */
public class InstanceList {

	// IVAR instances - The instances

	private Vector<Instance> instances;

	/**
	 * Creates a new instance list
	 * @param predicates	The predicates to use
	 * @param variables		The number of variables to be used
	 */
	public InstanceList(Vector<Predicate> predicates, int variables) {
		this.instances = getInstances(predicates, variables);
	}

	/**
	 * Returns the instance with the given index
	 * @param index	The index of the instance
	 * @return	An instance
	 */
	public Instance get(int index) {
		return this.instances.get(index);
	}

	/**
	 * Returns the size of this instance list
	 * @return	The size of this instance list
	 */
	public int size() {
		return this.instances.size();
	}

	/**
	 * Returns an instance of this list
	 * @param index		The index of the instance
	 * @param inBody	Whether or not the instance should be a body or head instance
	 * @return	A positioned instance
	 */
	public PositionedInstance getInstance(int index, boolean inBody) {
		return new PositionedInstance(this, inBody, index);
	}

	private Vector<Instance> getInstances(Vector<Predicate> predicates, int variables) {
		Vector<InstanceSetPrototype> instanceSetPrototypes = InstanceSetPrototype.createInstanceSets(predicates);
		List<Instance> instanceList = new ArrayList<>();
		for(Numbers.Permutation choice : getChoices(variables, instanceSetPrototypes.length)) {
			InstanceSetPrototype instanceSetPrototype = instanceSetPrototypes.get(choice.getDistinctCount() - 1);
			instanceList.addAll(instanceSetPrototype.getInstances(choice.getArray()));
		}
		return new Vector<>(instanceList.toArray(new Instance[instanceList.size()]));
	}

	private List<Numbers.Permutation> getChoices(int variables, int maxArity) {
		List<Numbers.Permutation> choices = new ArrayList<>();
		for(int i = 0; i < maxArity; i++)
			if(i + 1 <= variables)
				choices.addAll(Numbers.getChoices(variables, i + 1));
		choices.sort(new ClauseComparator());
		return choices;
	}
}
