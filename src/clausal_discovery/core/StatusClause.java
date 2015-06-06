package clausal_discovery.core;

import basic.StringUtil;
import clausal_discovery.instance.Instance;
import clausal_discovery.instance.InstanceComparator;
import clausal_discovery.instance.InstanceList;
import clausal_discovery.instance.PositionedInstance;
import logic.expression.formula.Formula;
import util.Numbers;
import vector.Vector;

import java.util.*;
import java.util.stream.Collectors;

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

	private final Vector<PositionedInstance> instances;

	public Vector<PositionedInstance> getInstances() {
		return instances;
	}

	public int getLength() {
		return getInstances().size();
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
	 */
	public StatusClause() {
		this.rank = 0;
		this.instances = new Vector<>();
		this.environment = new Environment();
	}

	private StatusClause(int rank, Vector<PositionedInstance> instances, Environment environment) {
		this.rank = rank;
		this.instances = instances;
		this.environment = environment;
	}

	// endregion

	// region Public methods

	/**
	 * Returns whether this status clause is currently in the body
	 * @return	True iff the status clause is currently in the body
	 */
	public boolean inBody() {
		return getInstances().isEmpty() || getInstances().getLast().isInBody();
	}

	/**
	 * Returns the index of the last instance
	 * @return The index of the last instance or -1 if none such instance exists
	 */
	public int getIndex() {
		return getInstances().isEmpty() ? -1 : getInstances().getLast().getIndex();
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
		return Optional.of(new StatusClause(newRank, getInstances().grow(instance), newEnvironment));
	}

	/**
	 * Determines whether this status clause is a subset of the given status clause
	 * @param statusClause	The clause to check
	 * @return	True iff this clause equals a subset of the given status clause of the same length as this clause
	 */
	public boolean isSubsetOf(StatusClause statusClause) {
		for(StatusClause clause : statusClause.getSubsets(getLength()))
			if(equalsSymmetric(clause))
				return true;
		return false;
		/*for(int i = 0; i <= statusClause.getLength() - getLength(); i++) {
			Optional<StatusClause> optionalClause = statusClause.getSubsetClause(i, getLength());
			if((""+this).equals("row(0, 1) & row(0, 2) => false") && (""+statusClause).equals("row(0, 1) & value(0, 1) & row(0, 2) => false"))
				Log.LOG.printLine(i+" "+optionalClause.get());
			if(optionalClause.isPresent() && equalsSymmetric(optionalClause.get()))
				return true;
		}
		return false;*/
	}

	private List<StatusClause> getSubsets(int size) {
		List<StatusClause> result = new ArrayList<>();
		Numbers.getChoices(getLength(), size).forEach(p -> getSubsetClause(p).ifPresent(result::add));
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		StatusClause clause = (StatusClause) o;
		return instances.equals(clause.instances);

	}

	@Override
	public int hashCode() {
		return instances.hashCode();
	}

	@Override
	public String toString() {
		List<String> head = getInstances().stream()
				.filter(i -> !i.isInBody())
				.map(pi -> "" + pi.getInstance())
				.collect(Collectors.toList());
		List<String> body = getInstances().stream()
				.filter(PositionedInstance::isInBody)
				.map(pi -> "" + pi.getInstance())
				.collect(Collectors.toList());
		return (body.isEmpty() ? "true" : StringUtil.join(" & ", body)) + " => "
				+ (head.isEmpty() ? "false" : StringUtil.join(" | ", head));
	}

	public Formula getFormula() {
		return new StatusClauseConverter().apply(this);
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
		if(inBody() == positionedInstance.isInBody() && positionedInstance.getIndex() <= getIndex())
			return false;
		if(!inBody() && positionedInstance.isInBody())
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
		if(!instance.getDefinition().isSymmetric())
			return containedInstance.getVariableIndices().equals(instance.getVariableIndices());
		return containedInstance.getVariableIndices().sortedCopy().equals(instance.getVariableIndices().sortedCopy());
	}

	private boolean isConnected(Vector<Integer> indices) {
		int max = getRank() - 1;
		for(int i = 0; i < indices.size(); i++)
			if(indices.get(i) <= max)
				return true;
		return false;
	}

	private boolean introducesVariablesInOrder(PositionedInstance instance) {
		int max = getRank() - 1;
		Vector<Integer> indices = instance.getInstance().getVariableIndices();
		for(int i = 0; i < indices.size(); i++)
			if((instance.isInBody()/*/ || (getLength() == 0)/**/) && indices.get(i) == max + 1)
				max = indices.get(i);
			else if(indices.get(i) > max)
				return false;
		return true;
	}

	protected boolean isRepresentative() {
		List<Numbers.Permutation> permutations = Numbers.getPermutations(getLength());
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

	private Optional<StatusClause> getSubsetClause(int start, int length) {
		List<PositionedInstance> instances = new ArrayList<>(length);
		for(int i = start; i < start + length; i++)
			instances.add(getInstances().get(i));
		return getClause(instances);
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
		InstanceComparator comparator = new InstanceComparator();
		for(int i = 0; i < getInstances().length; i++) {
			int compare = comparator.compare(newClause.getInstances().get(i), getInstances().get(i));
			if(compare < 0)
				return false;
			else if(compare > 0)
				return true;
		}
		return true;
	}

	private Optional<StatusClause> buildClause(List<PositionedInstance> instances) {
		Optional<StatusClause> clause = Optional.of(new StatusClause());
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
