package clausal_discovery.instance;

import clausal_discovery.core.Environment;
import clausal_discovery.core.PredicateDefinition;
import util.Numbers;
import vector.SafeList;

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

	private final SafeList<InstancePrototype> prototypes;

	public SafeList<InstancePrototype> getPrototypes() {
		return prototypes;
	}

	//endregion

	//region Construction

	protected InstanceSetPrototype(SafeList<InstancePrototype> prototypes) {
		this.rank = prototypes.isEmpty() ? 0 : prototypes.get(0).getRank();
		for(int i = 1; i < prototypes.size(); i++) assert prototypes.get(i).getRank() == getRank();
		this.prototypes = prototypes;
	}

	//endregion

	//region Public methods
	public SafeList<Instance> getInstances(int[] indices) {
		return getPrototypes().map(p -> p.instantiate(indices));
	}

	public static SafeList<InstanceSetPrototype> createInstanceSets(SafeList<PredicateDefinition> definitions) {
		int maxArity = definitions.get(0).getArity();
		for(int i = 1; i < definitions.size(); i++)
			maxArity = Math.max(maxArity, definitions.get(i).getArity());

		List<InstanceSetPrototype> instanceSetPrototypes = new ArrayList<>();
		List<PredicateDefinition> definitionsSet = new ArrayList<>(definitions);
		for(int i = 0; i < maxArity; i++) {
			List<PredicateDefinition> newSet = new ArrayList<>();
			for(PredicateDefinition definition : definitionsSet)
				if(definition.getArity() > i)
					newSet.add(definition);
			definitionsSet = newSet;
			InstanceSetPrototype instanceSet = createInstanceSet(definitionsSet, i + 1);
			instanceSetPrototypes.add(instanceSet);
		}
		return new SafeList<>(instanceSetPrototypes);
	}

	public static InstanceSetPrototype createInstanceSet(Collection<PredicateDefinition> definitions, int rank) {
		List<InstancePrototype> prototypes = new ArrayList<>();
		for(PredicateDefinition definition : definitions) {
			List<Numbers.Permutation> permutations = Numbers.take(rank, definition.getArity());
			for(Numbers.Permutation permutation : permutations)
				if(new Environment().isValidInstance(definition, SafeList.from(permutation.getIntegerArray())))
					if(!definition.isSymmetric() || permutation.isSorted())
						prototypes.add(new InstancePrototype(definition, permutation));
		}
		return new InstanceSetPrototype(SafeList.from(prototypes.toArray(new InstancePrototype[prototypes.size()])));
	}
	//endregion
}
