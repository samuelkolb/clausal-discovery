package clausal_discovery.instance;

import clausal_discovery.core.Environment;
import clausal_discovery.core.PredicateDefinition;
import util.Numbers;
import vector.Vector;
import vector.WriteOnceVector;
import logic.bias.Type;
import logic.expression.formula.Predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An instance set prototype contains prototypes of the same rank
 *
 * @author Samuel Kolb
 */
public class InstanceSetPrototype {

	//region Variables
	private final int rank;

	public int getRank() {
		return rank;
	}

	private final Vector<InstancePrototype> prototypes;

	public Vector<InstancePrototype> getPrototypes() {
		return prototypes;
	}

	//endregion

	//region Construction

	protected InstanceSetPrototype(Vector<InstancePrototype> prototypes) {
		this.rank = prototypes.get(0).getRank();
		for(int i = 1; i < prototypes.size(); i++) assert prototypes.get(i).getRank() == getRank();
		this.prototypes = prototypes;
	}

	//endregion

	//region Public methods
	public Vector<Instance> getInstances(int[] indices) {
		assert indices.length == getRank();
		Vector<Instance> instances = new WriteOnceVector<>(new Instance[getPrototypes().size()]);
		for(InstancePrototype prototype : getPrototypes())
			instances.add(prototype.instantiate(indices));
		return instances;
	}

	public static Vector<InstanceSetPrototype> createInstanceSets(Vector<PredicateDefinition> definitions) {
		int maxArity = definitions.get(0).getArity();
		for(int i = 1; i < definitions.size(); i++)
			maxArity = Math.max(maxArity, definitions.get(i).getArity());

		Vector<InstanceSetPrototype> instanceSetPrototypes = new WriteOnceVector<>(new InstanceSetPrototype[maxArity]);
		List<PredicateDefinition> definitionsSet = new ArrayList<>(definitions);
		for(int i = 0; i < maxArity; i++) {
			List<PredicateDefinition> newSet = new ArrayList<>();
			for(PredicateDefinition definition : definitionsSet)
				if(definition.getArity() > i)
					newSet.add(definition);
			definitionsSet = newSet;
			instanceSetPrototypes.add(createInstanceSet(definitionsSet, i + 1));
		}
		return instanceSetPrototypes;
	}

	public static InstanceSetPrototype createInstanceSet(Collection<PredicateDefinition> definitions, int rank) {
		List<InstancePrototype> prototypes = new ArrayList<>();
		for(PredicateDefinition definition : definitions) {
			List<Numbers.Permutation> permutations = Numbers.take(rank, definition.getArity());
			for(Numbers.Permutation permutation : permutations)
				if(new Environment().isValidInstance(definition, new Vector<>(permutation.getIntegerArray())))
					prototypes.add(new InstancePrototype(definition, permutation));
		}
		return new InstanceSetPrototype(new Vector<>(prototypes.toArray(new InstancePrototype[prototypes.size()])));
	}

	public static void main(String[] args) {
		Type type = new Type("H");
		PredicateDefinition d = new PredicateDefinition(new Predicate("p", type, Type.UNDEFINED, new Type("T"), type));
		System.out.println(new Environment().isValidInstance(d, new Vector<Integer>(1, 1, 3, 2)));
	}
	//endregion
}
