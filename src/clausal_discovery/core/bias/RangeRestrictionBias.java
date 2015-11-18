package clausal_discovery.core.bias;

import cern.colt.bitvector.BitVector;
import clausal_discovery.core.AtomSet;
import clausal_discovery.core.Literal;
import clausal_discovery.core.LiteralSet;
import clausal_discovery.instance.InstanceList;
import log.Log;

/**
 * Created by samuelkolb on 18/11/15.
 *
 * @author Samuel Kolb
 */
public class RangeRestrictionBias implements BiasModule {

	private final int rank;

	public int getRank() {
		return rank;
	}

	private final InstanceList instanceList;

	/**
	 * Creates a range restriction bias.
	 * Initially no variables have been seen.
	 * @param instanceList	The instance list
	 */
	public RangeRestrictionBias(InstanceList instanceList) {
		this(-1, instanceList);
	}

	/**
	 * Creates a range restriction bias.
	 * @param rank    		The highest variable number that is allowed.
	 * @param instanceList	The instance list
	 */
	public RangeRestrictionBias(int rank, InstanceList instanceList) {
		this.rank = rank;
		this.instanceList = instanceList;
	}

	@Override
	public LiteralSet getMask() {
		BitVector vector = new BitVector(this.instanceList.size());
		vector.not();
		return new LiteralSet(new AtomSet(this.instanceList, vector), getRankSet().getHead());
	}

	protected LiteralSet getRankSet() {
		return getRank() < 0 ? new LiteralSet(this.instanceList) : this.instanceList.getRankSet(this.rank);
	}

	@Override
	public BiasModule extend(Literal literal) {
		return new RangeRestrictionBias(Math.max(this.rank, literal.getRank()), instanceList);
	}
}
