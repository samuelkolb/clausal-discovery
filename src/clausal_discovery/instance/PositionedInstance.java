package clausal_discovery.instance;

/**
 * Created by samuelkolb on 12/04/15.
 *
 * @author Samuel Kolb
 */
public class PositionedInstance {

	// IVAR instance - The instance list used for clause ordering

	private final InstanceList instanceList;

	public InstanceList getInstanceList() {
		return instanceList;
	}

	// IVAR inBody - Whether this instance is in the body of a clause

	private final boolean inBody;

	public boolean isInBody() {
		return inBody;
	}

	// IVAR index - The index of this instance

	private final int index;

	public int getIndex() {
		return index;
	}

	public Instance getInstance() {
		return getInstanceList().get(getIndex());
	}

	/**
	 * Creates a new positioned instance
	 * @param instanceList	The instance list
	 * @param inBody		Whether this instance is a body or a head instance
	 * @param index			The index of the instance within the instance list
	 */
	PositionedInstance(InstanceList instanceList, boolean inBody, int index) {
		this.instanceList = instanceList;
		this.inBody = inBody;
		this.index = index;
	}

	/**
	 * Returns a clone of this instance where the inBody property is set to the given value
	 * @param inBody	The new inBody value
	 * @return	| return.getInstance() == this.getInstance()
	 * 			| && return.isInBody() == inBody
	 */
	public PositionedInstance clone(boolean inBody) {
		return new PositionedInstance(getInstanceList(), inBody, getIndex());
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		PositionedInstance instance = (PositionedInstance) o;

		return inBody == instance.inBody && index == instance.index;

	}

	@Override
	public int hashCode() {
		int result = (inBody ? 1 : 0);
		result = 31 * result + index;
		return result;
	}

	@Override
	public String toString() {
		return (isInBody() ? "~" : "") + getInstance();
	}
}
