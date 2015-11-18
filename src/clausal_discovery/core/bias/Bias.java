package clausal_discovery.core.bias;

import clausal_discovery.core.LiteralSet;
import clausal_discovery.instance.PositionedInstance;
import vector.SafeList;

import java.util.List;

/**
 * Created by samuelkolb on 18/11/15.
 *
 * @author Samuel Kolb
 */
public class Bias {

	private final LiteralSet unlocked;

	private final LiteralSet maskedUnlocked;

	private final LiteralSet blocked;

	private final SafeList<BiasModule> modules;

	/**
	 * Creates a bias with a set of unlocked and blocked literals, as well as a list of bias modules.
	 * @param unlocked	The unlocked literals
	 * @param blocked	The blocked literals
	 * @param modules	The bias modules
	 */
	public Bias(LiteralSet unlocked, LiteralSet blocked, List<BiasModule> modules) {
		this.unlocked = unlocked.minus(blocked);
		this.blocked = blocked;
		this.modules = SafeList.from(modules);
		this.maskedUnlocked = this.modules.foldLeft(this.unlocked, (ls, m) -> ls.intersect(m.getMask()));
	}

	/**
	 * Creates a new bias using the given literal.
	 * @param literal	The literal
	 * @return	An updated bias
	 */
	public Bias process(PositionedInstance literal) {
		LiteralSet newUnlocked = this.unlocked.union(literal.getEnabledSet());
		LiteralSet newBlocked = this.blocked.union(literal.getDisableSet());
		return new Bias(newUnlocked, newBlocked, modules.map(m -> m.extend(literal)));
	}

	/**
	 * Checks the given literal.
	 * @param literal	The literal to check
	 * @return	True iff this bias allows the given literal
	 */
	public boolean allows(PositionedInstance literal) {
		return this.maskedUnlocked.contains(literal.getIndex(), literal.isInBody());
	}

	@Override
	public String toString() {
		return this.maskedUnlocked.toString();
	}
}
