package clausal_discovery;

import util.Numbers;
import vector.Vector;
import vector.WriteOnceVector;
import logic.bias.Type;
import logic.expression.formula.Predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by samuelkolb on 18/02/15.
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

	public static Vector<InstanceSetPrototype> createInstanceSets(Vector<Predicate> predicates) {
		int maxArity = predicates.get(0).getArity();
		for(int i = 1; i < predicates.size(); i++)
			maxArity = Math.max(maxArity, predicates.get(i).getArity());

		Vector<InstanceSetPrototype> instanceSetPrototypes = new WriteOnceVector<>(new InstanceSetPrototype[maxArity]);
		List<Predicate> predicateSet = new ArrayList<>(predicates);
		for(int i = 0; i < maxArity; i++) {
			List<Predicate> newSet = new ArrayList<>();
			for(Predicate predicate : predicateSet)
				if(predicate.getArity() > i)
					newSet.add(predicate);
			predicateSet = newSet;
			instanceSetPrototypes.add(createInstanceSet(predicateSet, i + 1));
		}
		return instanceSetPrototypes;
	}

	public static InstanceSetPrototype createInstanceSet(Collection<Predicate> predicateSet, int rank) {
		List<InstancePrototype> prototypes = new ArrayList<>();
		for(Predicate predicate : predicateSet) {
			List<Numbers.Permutation> permutations = Numbers.take(rank, predicate.getArity());
			for(Numbers.Permutation permutation : permutations)
				if(new Environment().isValidInstance(predicate, permutation.getIntegerArray()))
					prototypes.add(new InstancePrototype(predicate, permutation));
		}
		return new InstanceSetPrototype(new Vector<>(prototypes.toArray(new InstancePrototype[prototypes.size()])));
	}

	public static void main(String[] args) {
		Type type = new Type("H");
		Predicate p = new Predicate("p", type, Type.UNDEFINED, new Type("T"), type);
		System.out.println(new Environment().isValidInstance(p, new Integer[]{1, 1, 3, 2}));
	}
	//endregion
}
