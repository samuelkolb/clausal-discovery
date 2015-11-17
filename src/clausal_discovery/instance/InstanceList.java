package clausal_discovery.instance;

import association.HashPairing;
import association.Pairing;
import basic.StringUtil;
import clausal_discovery.core.LiteralSet;
import clausal_discovery.core.PredicateDefinition;
import clausal_discovery.core.bias.Bias;
import clausal_discovery.core.bias.ConnectedInstanceBias;
import clausal_discovery.core.bias.InstanceBias;
import clausal_discovery.core.bias.OrderedInstanceBias;
import util.Numbers;
import vector.SafeList;
import vector.SafeListBuilder;
import vector.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a list of instances to reduce duplicates while building clauses in an incremental fashion.
 *
 * @author Samuel Kolb
 */
public class InstanceList {

	// IVAR pairing - The mapping between indices and instances

	private final Pairing<Integer, Instance> pairing;

	private final InstanceBias instanceBias = new ConnectedInstanceBias().combineWith(new OrderedInstanceBias());

	private final Bias bias = new OrderedInstanceBias();

	public InstanceBias getInstanceBias() {
		return instanceBias;
	}

	private final SafeList<PositionedInstance> bodyAtoms;

	private final SafeList<PositionedInstance> headAtoms;

	private final LiteralSet enabled;

	public LiteralSet getEnabled() {
		return enabled;
	}

	private final LiteralSet disabled;

	public LiteralSet getDisabled() {
		return disabled;
	}

	/**
	 * Creates a new instance list
	 * @param predicates	The predicates to use
	 * @param variables		The number of variables to be used
	 */
	public InstanceList(Vector<PredicateDefinition> predicates, int variables) {
		this.pairing = getInstances(predicates, getMaximalVariables(variables, predicates));
		SafeListBuilder<PositionedInstance> body = SafeList.build(size());
		SafeListBuilder<PositionedInstance> head = SafeList.build(size());
		LiteralSet enabled = new LiteralSet(this);
		LiteralSet disabled = new LiteralSet(this);
		for(int i = 0; i < size(); i++) {
			for(boolean inBody : new boolean[]{true, false}) {
				if(bias.enables(get(i), inBody)) {
					enabled = enabled.add(i, inBody);
				}
				if(bias.disables(get(i), inBody)) {
					disabled = disabled.add(i, inBody);
				}
				LiteralSet enabledSet = new LiteralSet(this);
				LiteralSet disabledSet = new LiteralSet(this);
				disabledSet = disabledSet.add(i, !inBody);
				for(int j = 0; j < size(); j++) {
					// Use order and bias to calculated enabled and disabled instances
					if(j <= i) {
						disabledSet = disabledSet.add(j, inBody);
					}
					if(!inBody) {
						disabledSet = disabledSet.add(j, false);
					}
					// not-opt j < i not necessary
					for(boolean testInBody : new boolean[]{true, false}) {
						if(getInstanceBias().enables(get(i), inBody, get(j), testInBody)) {
							enabledSet = enabledSet.add(j, testInBody);
						}
						if(getInstanceBias().disables(get(i), inBody, get(j), testInBody)) {
							disabledSet = disabledSet.add(j, testInBody);
						}
					}
				}
				(inBody ? body : head).add(new PositionedInstance(this, inBody, i, enabledSet, disabledSet));
			}
		}
		this.bodyAtoms = body.create();
		this.headAtoms = head.create();
		this.disabled = disabled;
		this.enabled = enabled.minus(disabled);
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
		return (inBody ? this.bodyAtoms : this.headAtoms).get(index);
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

	private Pairing<Integer, Instance> getInstances(Vector<PredicateDefinition> definitions, int variables) {
		Vector<InstanceSetPrototype> instanceSetPrototypes = InstanceSetPrototype.createInstanceSets(definitions);
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

	private int getMaximalVariables(int variables, Vector<PredicateDefinition> predicates) {
		int max = 0;
		for(PredicateDefinition definition : predicates)
			max = Math.max(max, definition.getPredicate().getArity());
		return  max > 1 ? variables : 1;
	}

	@Override
	public String toString() {
		Set<Integer> keys = this.pairing.keySet();
		return StringUtil.join(", ", keys.stream().map(key -> key + ": " + get(key)).collect(Collectors.toList()));
	}
}
