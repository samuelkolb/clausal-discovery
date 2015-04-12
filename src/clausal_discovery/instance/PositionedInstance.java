package clausal_discovery.instance;

import vector.Vector;

/**
 * Created by samuelkolb on 12/04/15.
 *
 * @author Samuel Kolb
 */
public class PositionedInstance {

	// IVAR instance - The instance list used for clause ordering

	private final Vector<Instance> instances;

	public Vector<Instance> getInstances() {
		return instances;
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
		return getInstances().get(getIndex());
	}

	public PositionedInstance(Vector<Instance> instances, boolean inBody, int index) {
		this.instances = instances;
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
		return new PositionedInstance(getInstances(), inBody, getIndex());
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
}
