package clausal_discovery;

import clausal_discovery.instance.PositionedInstance;
import vector.Vector;

import java.util.Optional;

/**
 * Represents a selection of indices that represent instances in the instances body and head
 * The status clause helps with the efficient traversal of the clausal space
 * // TODO Improvement by arranging instance sets in a graph to determine possible moves
 * @author Samuel Kolb
 */
public class StatusClause {

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


	/**
	 * Returns whether this status clause is currently in the body
	 * @return	True iff the status clause is currently in the body
	 */
	public boolean inBody() {
		return getInstances().isEmpty() || getInstances().getLast().isInBody();
	}

	public int getIndex() {
		return getInstances().isEmpty() ? -1 : getInstances().getLast().getIndex();
	}

	/**
	 * Returns whether this clause contains the given index-boolean pair
	 * @param instance	The instance representation
	 * @return	True iff this instance has been added to this status clause already
	 */
	public boolean contains(PositionedInstance instance) {
		return getInstances().contains(instance);
	}

	/**
	 * Returns whether the given instance can be added
	 * @param instance	A positioned instance
	 * @return	True iff the given instance is 1) consistent with typing, 2) connected 3) introduces variables in
	 * 			order and only it is in the body, and 4) an instance added to the head does not appear in the body
	 * 		TODO correct documentation
	 */
	public boolean canProcess(PositionedInstance instance) {
		if(inBody() == instance.isInBody() && instance.getIndex() <= getIndex())
			return false;
		if(!inBody() && instance.isInBody())
			return false;
		if(!getEnvironment().isValidInstance(instance.getInstance()))
			return false;
		if(!inBody() && contains(instance.clone(false)))
			return false;
		Vector<Integer> indices = instance.getInstance().getVariableIndices();
		return (getRank() == 0 || isConnected(indices)) && introducesVariablesInOrder(instance);
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
			if(instance.isInBody() && indices.get(i) == max + 1)
				max = indices.get(i);
			else if(indices.get(i) > max)
				return false;
		return true;
	}

	/**
	 * Returns a new status clause where the given instance has been added
	 * @param instance	The instance to add
	 * @return	The new status clause
	 */
	public Optional<StatusClause> process(PositionedInstance instance) {
		if(!canProcess(instance))
			return Optional.empty();
		int newRank = Math.max(getRank(), instance.getInstance().getMax() + 1);
		Environment newEnvironment = getEnvironment().addInstance(instance.getInstance());
		return Optional.of(new StatusClause(newRank, getInstances().grow(instance), newEnvironment));
	}

	@Override
	public String toString() {
		return "StatusClause[" + getInstances() + "]";
	}
}
