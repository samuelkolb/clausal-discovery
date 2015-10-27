package clausal_discovery.core;

import basic.StringUtil;
import cern.colt.bitvector.BitVector;
import cern.colt.function.IntProcedure;
import clausal_discovery.instance.Instance;
import clausal_discovery.instance.InstanceComparator;
import clausal_discovery.instance.InstanceList;
import clausal_discovery.instance.PositionedInstance;
import logic.expression.formula.Formula;
import util.Numbers;
import vector.Vector;
import vector.WriteOnceVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntPredicate;

/**
 * Represents a selection of indices that represent instances in the instances body and head
 * The status clause helps with the efficient traversal of the clausal space
 * // TODO Improvement by arranging instance sets in a graph to determine possible moves
 * @author Samuel Kolb
 */
public class StatusClause {

	private class AtomSet implements Comparable<AtomSet> {

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

		private InstanceList instanceList;

		public InstanceList getInstanceList() {
			return instanceList;
		}

		private BitVector vector;

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

		public Vector<Instance> getInstances() {
			Vector<Instance> instances = new WriteOnceVector<>(new Instance[size()]);
			forEach(i -> instances.add(instanceList.get(i)));
			return instances;
		}

		public Vector<PositionedInstance> getInstances(boolean inBody) {
			Vector<PositionedInstance> instances = new WriteOnceVector<>(new PositionedInstance[size()]);
			forEach(i -> instances.add(instanceList.getInstance(i, inBody)));
			return instances;
		}

		public void forEach(IntPredicate predicate) {
			vector.forEachIndexFromToInState(0, vector.size() - 1, true, predicate::test);
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

	private class LiteralSet implements Comparable<LiteralSet> {

		private AtomSet body;

		private AtomSet head;

		public LiteralSet(InstanceList list) {
			this.body = new AtomSet(list);
			this.head = new AtomSet(list);
		}

		private LiteralSet(AtomSet body, AtomSet head) {
			this.body = body;
			this.head = head;
		}

		public LiteralSet add(int index, boolean inBody) {
			if(inBody) {
				AtomSet newSet = new AtomSet(body);
				newSet.add(index);
				return new LiteralSet(newSet, head);
			} else {
				AtomSet newSet = new AtomSet(head);
				newSet.add(index);
				return new LiteralSet(body, newSet);
			}
		}

		public int size() {
			return body.size() + head.size();
		}

		public boolean isEmpty() {
			return body.isEmpty() && head.isEmpty();
		}

		public boolean isInBody() {
			return head.isEmpty();
		}

		@Override
		public boolean equals(Object o) {
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;

			LiteralSet literalSet = (LiteralSet) o;
			return body.equals(literalSet.body) && head.equals(literalSet.head);

		}

		@Override
		public int hashCode() {
			int result = body.hashCode();
			result = 31 * result + head.hashCode();
			return result;
		}

		@Override
		public int compareTo(LiteralSet o) {
			int bodyCompare = body.compareTo(o.body);
			return bodyCompare == 0 ? head.compareTo(head) : bodyCompare;
		}
	}

	// region Variables

	// IVAR rank - The rank is the amount of variables already introduced

	private final int rank;

	public int getRank() {
		return rank;
	}

	// IVAR instances - The instances in this clause

	private final LiteralSet literalSet;

	protected LiteralSet getLiteralSet() {
		return literalSet;
	}

	// IVAR environment - The typing environment

	private final Environment environment;

	public Environment getEnvironment() {
		return environment;
	}

	// endregion

	// region Construction

	/**
	 * Creates a new status clause
	 * @param instanceList	The instance list
	 */
	public StatusClause(InstanceList instanceList) {
		this.rank = 0;
		this.literalSet = new LiteralSet(instanceList);
		this.environment = new Environment();
	}

	private StatusClause(int rank, LiteralSet literalSet, Environment environment) {
		this.rank = rank;
		this.literalSet = literalSet;
		this.environment = environment;
	}

	// endregion

	// region Public methods

	/**
	 * Returns whether this status clause is currently in the body
	 * @return	True iff the status clause is currently in the body
	 */
	public boolean isInBody() {
		return getLiteralSet().isInBody();
	}

	/**
	 * Returns the index of the last instance
	 * @return The index of the last instance or -1 if none such instance exists
	 */
	public int getIndex() {
		return isInBody() ? getLiteralSet().body.lastIndex() : getLiteralSet().head.lastIndex();
	}

	/**
	 * Returns a vector of positioned instances.
	 * @return	A vector
	 * // TODO consider removal
	 */
	@Deprecated
	public Vector<PositionedInstance> getInstances() {
		Vector<PositionedInstance> vector = new WriteOnceVector<>(new PositionedInstance[size()]);
		vector.addAll(getLiteralSet().body.getInstances(true));
		vector.addAll(getLiteralSet().head.getInstances(false));
		return vector;
	}

	/**
	 * Creates a new clause by adding the given instance
	 * @param instance	The instance to add
	 * @return	An optional containing either the valid representative clause or an empty optional
	 */
	public Optional<StatusClause> processIfRepresentative(PositionedInstance instance) {
		Optional<StatusClause> clause = addIfValid(instance);
		if(clause.isPresent() && clause.get().isRepresentative())
			return clause;
		return Optional.empty();
	}

	/**
	 * Creates a new clause by adding the given instance
	 * @param instance	The instance to add
	 * @return	An optional containing either the valid clause or an empty optional
	 */
	public Optional<StatusClause> addIfValid(PositionedInstance instance) {
		if(!canAdd(instance))
			return Optional.empty();
		int newRank = Math.max(getRank(), instance.getInstance().getMax() + 1);
		Environment newEnvironment = getEnvironment().addInstance(instance.getInstance());
		LiteralSet newLiteralSet = literalSet.add(instance.getIndex(), instance.isInBody());
		return Optional.of(new StatusClause(newRank, newLiteralSet, newEnvironment));
	}

	/**
	 * Determines whether this status clause is a subset of the given status clause
	 * @param statusClause	The clause to check
	 * @return	True iff this clause equals a subset of the given status clause of the same length as this clause
	 */
	public boolean isSubsetOf(StatusClause statusClause) {
		for(StatusClause clause : statusClause.getSubsets(literalSet.size()))
			if(equalsSymmetric(clause))
				return true;
		return false;
	}

	private List<StatusClause> getSubsets(int size) {
		List<StatusClause> result = new ArrayList<>();
		Numbers.getChoices(literalSet.size(), size).forEach(p -> getSubsetClause(p).ifPresent(result::add));
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		StatusClause that = (StatusClause) o;
		return literalSet.equals(that.literalSet);

	}

	@Override
	public int hashCode() {
		return literalSet.hashCode();
	}

	@Override
	public String toString() {
		List<String> head = literalSet.head.getInstances().map(String.class, Instance::toString);
		List<String> body = literalSet.body.getInstances().map(String.class, Instance::toString);
		return (body.isEmpty() ? "true" : StringUtil.join(" & ", body)) + " => "
				+ (head.isEmpty() ? "false" : StringUtil.join(" | ", head));
	}

	public Formula getFormula() {
		return new StatusClauseConverter().apply(this);
	}

	/**
	 * Returns an empty clause (with the same instance list).
	 * @return	An empty clause
	 */
	public StatusClause emptyClause() {
		return new StatusClause(literalSet.body.instanceList);
	}

	/**
	 * Returns the size of this clause.
	 * @return The number of literals in this clause
	 */
	public int size() {
		return getLiteralSet().size();
	}
	// endregion

	// region Private methods

	/**
	 * Returns whether adding the given instance will produce a valid clause
	 * @param positionedInstance	The instance to add
	 * @return True iff the given instance:
	 * 		1) does not occur in the instance list before the last instance of this clause
	 * 		2) is a body instance while this clause already containsInstance a head instance
	 * 		3) is consistent with the typing of this clause
	 * 		4) is not a head instance that has already been added as a body instance
	 * 		5) is connected
	 * 		6) introduces variables in order
	 */
	protected boolean canAdd(PositionedInstance positionedInstance) {
		Instance instance = positionedInstance.getInstance();
		// TODO containsInstance instance => sort variables to compare
		if(isInBody() == positionedInstance.isInBody() && positionedInstance.getIndex() <= getIndex())
			return false;
		if(!isInBody() && positionedInstance.isInBody())
			return false;
		if(!getEnvironment().isValidInstance(instance))
			return false;
		/*if(!positionedInstance.isInBody() && getInstances().contains(positionedInstance.clone(true)))
			return false;/*/
		if(containsInstance(positionedInstance.getInstance()))
			return false;/**/
		Vector<Integer> indices = instance.getVariableIndices();
		return (getRank() == 0 || isConnected(indices)) && introducesVariablesInOrder(positionedInstance);
	}

	protected boolean equalsSymmetric(StatusClause clause) {
		// not-opt revisit
		for(PositionedInstance instance : clause.getInstances())
			if(!containsElementSymmetric(instance))
				return false;
		return true;
	}

	protected boolean containsElementSymmetric(PositionedInstance containedInstance) {
		for(PositionedInstance instance : getInstances())
			if(equalsSymmetric(instance, containedInstance))
				return true;
		return false;
	}

	protected boolean equalsSymmetric(PositionedInstance instance1, PositionedInstance instance2) {
		return containsInstance(instance1, instance2.getInstance()) && instance1.isInBody() == instance2.isInBody();
	}

	/**
	 * Returns whether this clause containsInstance the given instance
	 * @param instance	The instance
	 * @return	True iff this instance has been added to this status clause already
	 */
	protected boolean containsInstance(Instance instance) {
		for(PositionedInstance positionedInstance : getInstances())
			if(containsInstance(positionedInstance, instance))
				return true;
		return false;
	}

	private boolean containsInstance(PositionedInstance positionedInstance, Instance instance) {
		Instance containedInstance = positionedInstance.getInstance();
		if(!containedInstance.getPredicate().equals(instance.getPredicate()))
			return false;
		return containedInstance.getVariableIndices().equals(instance.getVariableIndices());
	}

	private boolean isConnected(Vector<Integer> indices) {
		int max = getRank() - 1;
		for(Integer index : indices)
			if(index <= max)
				return true;
		return false;
	}

	private boolean introducesVariablesInOrder(PositionedInstance instance) {
		int max = getRank() - 1;
		Vector<Integer> indices = instance.getInstance().getVariableIndices();
		for(Integer index : indices)
			if((instance.isInBody()) && index == max + 1)
				max = index;
			else if(index > max)
				return false;
		return true;
	}

	protected boolean isRepresentative() {
		List<Numbers.Permutation> permutations = Numbers.getPermutations(size());
		for(Numbers.Permutation permutation : permutations) {
			List<PositionedInstance> instances = permutation.applyList(getInstances());
			if(!smallerThanOrEqual(instances))
				return false;
		}
		return true;
	}

	private boolean smallerThanOrEqual(List<PositionedInstance> instances) {
		Optional<StatusClause> builtClause = getClause(instances);
		return !builtClause.isPresent() || smallerThanOrEqual(builtClause.get());
	}

	private Optional<StatusClause> getSubsetClause(Numbers.Permutation permutation) {
		return getClause(permutation.applyList(getInstances()));
	}

	private Optional<StatusClause> getClause(List<PositionedInstance> instances) {
		Map<Integer, Integer> mapping = createMapping(instances);
		updateInstances(mapping, instances);
		instances.sort(new InstanceComparator());
		return buildClause(instances);
	}

	private boolean smallerThanOrEqual(StatusClause newClause) {
		return getLiteralSet().compareTo(newClause.getLiteralSet()) <= 0;
		/*InstanceComparator comparator = new InstanceComparator();
		for(int i = 0; i < getInstances().length; i++) {
			int compare = comparator.compare(newClause.getInstances().get(i), getInstances().get(i));
			if(compare < 0)
				return false;
			else if(compare > 0)
				return true;
		}
		return true;*/
	}

	private Optional<StatusClause> buildClause(List<PositionedInstance> instances) {
		Optional<StatusClause> clause = Optional.of(emptyClause());
		for(PositionedInstance instance : instances) {
			clause = clause.get().addIfValid(instance);
			if(!clause.isPresent())
				return clause;
		}
		return clause;
	}

	private Map<Integer,Integer> createMapping(List<PositionedInstance> instances) {
		int current = 0;
		Map<Integer, Integer> mapping = new HashMap<>();
		for(PositionedInstance instance : instances)
			for(Integer index : instance.getInstance().getVariableIndices())
				if(!mapping.containsKey(index))
					mapping.put(index, current++);
		return mapping;
	}

	private void updateInstances(Map<Integer, Integer> mapping, List<PositionedInstance> list) {
		for(int i = 0; i < list.size(); i++) {
			PositionedInstance positionedInstance = list.get(i);
			Instance instance = positionedInstance.getInstance();
			InstanceList instanceList = positionedInstance.getInstanceList();
			Instance newInstance = new Instance(instance.getDefinition(), getVariables(mapping, instance));
			list.set(i, instanceList.getInstance(instanceList.getIndex(newInstance), positionedInstance.isInBody()));
		}
	}

	private Vector<Integer> getVariables(Map<Integer, Integer> mapping, Instance instance) {
		return instance.getVariableIndices().map(Integer.class, mapping::get);
	}
	// endregion
}
