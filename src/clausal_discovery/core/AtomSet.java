package clausal_discovery.core;

import cern.colt.bitvector.BitVector;
import cern.colt.function.IntProcedure;
import clausal_discovery.instance.Instance;
import clausal_discovery.instance.InstanceList;
import clausal_discovery.instance.PositionedInstance;
import vector.SafeList;

import java.util.function.IntPredicate;

/**
 * Efficiently represents an ordered set of atoms from an atom list
 *
 * @author Samuel Kolb
 */
class AtomSet implements Comparable<AtomSet> {

	private class LastFunction implements IntProcedure {

		public int found = -1;

		@Override
		public boolean apply(int argument) {
			if(argument > found) {
				found = argument;
			} else {
				throw new IllegalStateException("Unexpected");
			}
			return true;
		}
	}

	private final InstanceList instanceList;

	public InstanceList getInstanceList() {
		return instanceList;
	}

	private final BitVector vector;

	private int size;

	public AtomSet(InstanceList instanceList) {
		this(instanceList, new BitVector(instanceList.size()));
	}

	public AtomSet(InstanceList instanceList, BitVector vector) {
		this.instanceList = instanceList;
		this.vector = vector;
		this.size = vector.cardinality();
	}

	public AtomSet(AtomSet atomSet) {
		this.instanceList = atomSet.instanceList;
		this.vector = atomSet.vector.copy();
		this.size = atomSet.size;
	}

	public boolean add(int atomIndex) {
		if(atomIndex < 0 || atomIndex >= instanceList.size()) {
			throw new IllegalArgumentException("Illegal atom index: " + atomIndex);
		}

		boolean wasInSet = vector.get(atomIndex);
		size += wasInSet ? 0 : 1;
		vector.set(atomIndex);
		return !wasInSet;
	}

	public int size() {
		return size;
	}

	public int lastIndex() {
		LastFunction last = new LastFunction();
		vector.forEachIndexFromToInState(0, vector.size() - 1, true, last);
		return last.found;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public SafeList<Instance> getInstances() {
		SafeList<Instance> instances = new WriteOnceVector<>(new Instance[size()]);
		forEach(i -> instances.add(instanceList.get(i)));
		return instances;
	}

	public SafeList<PositionedInstance> getInstances(boolean inBody) {
		SafeList<PositionedInstance> instances = new WriteOnceVector<>(new PositionedInstance[size()]);
		forEach(i -> instances.add(instanceList.getInstance(i, inBody)));
		return instances;
	}

	public boolean forEach(IntPredicate predicate) {
		return vector.forEachIndexFromToInState(0, vector.size() - 1, true, predicate::test);
	}

	public boolean isSubsetOf(AtomSet atomSet) {
		return !forEach(atomSet.vector::get);
	}

	@Override
	public int compareTo(AtomSet o) {
		BitVector comparisonVector = vector.copy();
		comparisonVector.xor(o.vector);
		if(comparisonVector.cardinality() == 0) {
			return 0;
		} else {
			int firstDifference = comparisonVector.indexOfFromTo(0, comparisonVector.size() - 1, true);
			return vector.get(firstDifference) ? -1 : 1;
		}
	}
}
