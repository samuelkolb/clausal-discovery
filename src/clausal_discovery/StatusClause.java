package clausal_discovery;

import util.Pair;
import vector.Vector;

/**
 * Represents a selection of indices that represent instances in the clauses body and head
 * The status clause helps with the efficient traversal of the clausal space
 * // TODO Improvement by arranging instance sets in a graph to determine possible moves
 * @author Samuel Kolb
 */
public class StatusClause {

	private final int rank;

	/**
	 * The rank is the amount of variables already introduced
	 * @return  The rank, an non-negative integer
	 */
	public int getRank() {
		return rank;
	}

	private final boolean body;

	/**
	 * Returns whether this status clause is currently in the body
	 * @return	True iff the status clause is currently in the body
	 */
	public boolean inBody() {
		return body;
	}

	private final int index;

	public int getIndex() {
		return index;
	}

	private final Vector<Pair<Integer, Boolean>> clauses;

	public Vector<Pair<Integer, Boolean>> getClauses() {
		return clauses;
	}

	public int getLength() {
		return getClauses().size();
	}

	private final Environment environment;

	/**
	 * Creates a new status clause
	 */
	public StatusClause() {
		this.index = -1;
		this.rank = 0;
		this.body = true;
		this.clauses = new Vector<>();
		this.environment = new Environment();
	}

	private StatusClause(int rank, boolean body, int index, Vector<Pair<Integer, Boolean>> clauses,
	                     Environment environment) {
		this.rank = rank;
		this.body = body;
		this.index = index;
		this.clauses = clauses;
		this.environment = environment;
	}

	/**
	 * Returns whether this clause contains the given index-boolean pair
	 * @param instance	The instance representation
	 * @return	True iff this instance has been added to this status clause already
	 */
	public boolean contains(Pair<Integer, Boolean> instance) {
		return getClauses().contains(instance);
	}

	/**
	 * Returns a status clause where new instances will be added to the head of the clause
	 * @return A new status clause
	 */
	public StatusClause enterHead() {
		if(!inBody())
			throw new IllegalStateException("Already in head");
		return new StatusClause(getRank(), false, getIndex(), getClauses(), environment);
	}

	/**
	 * Returns whether the given instance can be added
	 * @param instance	A predicate instance
	 * @return	True iff the given instance is 1) consistent with typing, 2) connected, and 3) introduces variables in
	 * 			order and only it is in the body
	 */
	public boolean canProcess(Instance instance) {
		if(!environment.isValidInstance(instance.getPredicate(), instance.getVariableIndices()))
			return false;
		Vector<Integer> indices = instance.getVariableIndices();
		int max = getRank() - 1;
		boolean connected = false;
		for(int i = 0; i < indices.size(); i++) {
			if(inBody() && indices.get(i) == max + 1)
				max += 1;
			else if(indices.get(i) > max)
				return false;
			else
				connected = true;
		}
		return connected || getRank() == 0;
	}

	/**
	 * Returns a new status clause where the given instance has been added
	 * @param index		The index of the instance to add
	 * @param instance	The instance to add
	 * @return	The new status clause
	 */
	public StatusClause process(int index, Instance instance) {
		if(!canProcess(instance))
			throw new IllegalArgumentException("Cannot process the given instance: " + instance);
		Pair.Implementation<Integer, Boolean> element = new Pair.Implementation<>(index, inBody());
		int newRank = Math.max(getRank(), instance.getMax() + 1);
		return new StatusClause(newRank, inBody(), index, clauses.grow(element), environment.addInstance(instance));
	}

	@Override
	public String toString() {
		return "StatusClause[" + clauses + "]";
	}
}
