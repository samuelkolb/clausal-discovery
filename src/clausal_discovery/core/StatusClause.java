package clausal_discovery.core;

import basic.StringUtil;
import clausal_discovery.instance.Instance;
import clausal_discovery.instance.InstanceComparator;
import clausal_discovery.instance.InstanceList;
import clausal_discovery.instance.PositionedInstance;
import log.Log;
import logic.expression.formula.Formula;
import util.Numbers;
import vector.SafeList;
import vector.WriteOnceSafeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a selection of indices that represent instances in the instances body and head
 * The status clause helps with the efficient traversal of the clausal space
 * // TODO Improvement by arranging instance sets in a graph to determine possible moves
 * @author Samuel Kolb
 */
public class StatusClause {

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
	 * Returns whether this clause has a head
	 * @return	True iff there are one or more atoms in the head of this clause
	 */
	public boolean hasHead() {
		return getLiteralSet().hasHead();
	}

	/**
	 * Returns the index of the last instance
	 * @return The index of the last instance or -1 if none such instance exists
	 */
	public int getIndex() {
		return hasHead() ? getLiteralSet().getHead().lastIndex() : getLiteralSet().getBody().lastIndex();
	}

	/**
	 * Returns a vector of positioned instances.
	 * @return	A vector
	 * // TODO consider removal
	 */
	@Deprecated
	public SafeList<PositionedInstance> getInstances() {
		SafeList<PositionedInstance> vector = new WriteOnceSafeList<>(new PositionedInstance[size()]);
		vector.addAll(getLiteralSet().getBody().getInstances(true));
		vector.addAll(getLiteralSet().getHead().getInstances(false));
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
		// not-opt
		//boolean test = statusClause.getLiteralSet().isSubsetOf(statusClause.getLiteralSet());
		for(StatusClause clause : statusClause.getSubsets(literalSet.size())) {
			if(equalsSymmetric(clause)) {
				return true;
			}
		}
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
		List<String> body = literalSet.getBody().getInstances().map(String.class, Instance::toString);
		List<String> head = literalSet.getHead().getInstances().map(String.class, Instance::toString);
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
		return new StatusClause(literalSet.getBody().getInstanceList());
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
		if(hasHead() == !positionedInstance.isInBody() && positionedInstance.getIndex() <= getIndex())
			return false;
		if(hasHead() && positionedInstance.isInBody())
			return false;
		if(!getEnvironment().isValidInstance(instance))
			return false;
		/*if(!positionedInstance.isInBody() && getInstances().contains(positionedInstance.clone(true)))
			return false;/*/
		if(containsInstance(positionedInstance.getInstance()))
			return false;/**/
		SafeList<Integer> indices = instance.getVariableIndices();
		return (getRank() == 0 || isConnected(indices)) && introducesVariablesInOrder(positionedInstance);
	}

	/**
	 * Determines whether this clause is equal to the given clause (up to symmetries).
	 * @param clause	The clause to check
	 * @return True iff the given clause is equal to this clause
	 */
	protected boolean equalsSymmetric(StatusClause clause) {
		// not-opt revisit
		for(PositionedInstance instance : clause.getInstances())
			if(!containsElementSymmetric(instance))
				return false;
		return true;
	}

	/**
	 * Determines whether this clause contains the given instance (up to symmetry).
	 * @param containedInstance	The instance to check
	 * @return	True iff this clause contains the instance or a symmetric variant
	 */
	protected boolean containsElementSymmetric(PositionedInstance containedInstance) {
		for(PositionedInstance instance : getInstances())
			if(equalsSymmetric(instance, containedInstance))
				return true;
		return false;
	}

	/**
	 * Determines whether two instances are equal (up to symmetry).
	 * @param instance1	The first instance
	 * @param instance2	The second instance
	 * @return	True iff the clauses are equal
	 */
	protected boolean equalsSymmetric(PositionedInstance instance1, PositionedInstance instance2) {
		// not-opt
		return containsInstance(instance1, instance2.getInstance()) && instance1.isInBody() == instance2.isInBody();
	}

	/**
	 * Returns whether this clause contains the given instance
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

	private boolean isConnected(SafeList<Integer> indices) {
		int max = getRank() - 1;
		for(Integer index : indices)
			if(index <= max)
				return true;
		return false;
	}

	private boolean introducesVariablesInOrder(PositionedInstance instance) {
		int max = getRank() - 1;
		SafeList<Integer> indices = instance.getInstance().getVariableIndices();
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

	private SafeList<Integer> getVariables(Map<Integer, Integer> mapping, Instance instance) {
		return instance.getVariableIndices().map(Integer.class, mapping::get);
	}
	// endregion
}
