package clausal_discovery.instance;

import association.HashPairing;
import association.Pairing;
import basic.StringUtil;
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

	// IVAR pairing - The mapping between indices and instances

	private Pairing<Integer, Instance> pairing;

	/**
	 * Creates a new instance list
	 * @param predicates	The predicates to use
	 * @param variables		The number of variables to be used
	 */
	public InstanceList(Vector<Predicate> predicates, int variables) {
		this.pairing = getInstances(predicates, getMaximalVariables(variables, predicates));
	}

	/**
	 * Returns the instance with the given index
	 * @param index	The index of the instance
	 * @return	An instance
	 */
	public Instance get(int index) {
		return this.pairing.getValue(index);
	}

	/**
	 * Returns the size of this instance list
	 * @return	The size of this instance list
	 */
	public int size() {
		return this.pairing.size();
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

	/**
	 * Finds the index of the given instance within this list
	 * @param instance	The instance to search
	 * @return	The index of the instance
	 */
	public int getIndex(Instance instance) {
		if(!this.pairing.containsValue(instance))
			throw new IllegalArgumentException("Instance list does not contain " + instance);
		return this.pairing.getKey(instance);
	}

	private Pairing<Integer, Instance> getInstances(Vector<Predicate> predicates, int variables) {
		Vector<InstanceSetPrototype> instanceSetPrototypes = InstanceSetPrototype.createInstanceSets(predicates);
		List<Instance> instanceList = new ArrayList<>();
		for(Numbers.Permutation choice : getChoices(variables, instanceSetPrototypes.length)) {
			InstanceSetPrototype instanceSetPrototype = instanceSetPrototypes.get(choice.getDistinctCount() - 1);
			instanceList.addAll(instanceSetPrototype.getInstances(choice.getArray()));
		}
		Pairing<Integer, Instance> pairing = new HashPairing<>(false, false);
		for(int i = 0; i < instanceList.size(); i++)
			pairing.put(i, instanceList.get(i));
		return pairing;
	}

	private List<Numbers.Permutation> getChoices(int variables, int maxArity) {
		List<Numbers.Permutation> choices = new ArrayList<>();
		for(int i = 0; i < maxArity; i++)
			if(i + 1 <= variables)
				choices.addAll(Numbers.getChoices(variables, i + 1));
		choices.sort(new ChoiceComparator());
		return choices;
	}

	private int getMaximalVariables(int variables, Vector<Predicate> predicates) {
		int max = 0;
		for(Predicate predicate : predicates)
			max = Math.max(max, predicate.getArity());
		return  max > 1 ? variables : 1;
	}

	@Override
	public String toString() {
		List<String> strings = new ArrayList<>();
		for(Integer key : this.pairing.keySet())
			strings.add(key + " => " + get(key));
		return StringUtil.join(", ", strings.toArray());
	}
}
