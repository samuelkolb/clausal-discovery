package clausal_discovery;

import util.Pair;
import vector.Vector;

/**
 * Created by samuelkolb on 22/02/15.
 * // TODO Improvement by arranging instance sets in a graph to determine possible moves
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

	public StatusClause enterHead() {
		if(!inBody())
			throw new IllegalStateException("Already in head");
		return new StatusClause(getRank(), false, getIndex(), getClauses(), environment);
	}

	public boolean canProcess(Instance instance) {
		if(!environment.isValidInstance(instance.getPredicate(), instance.getVariableIndices().getArray()))
			return false;
		Vector<Integer> indices = instance.getVariableIndices();//.sort(); // TODO review approach here
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
