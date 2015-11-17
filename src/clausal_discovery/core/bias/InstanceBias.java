package clausal_discovery.core.bias;

import clausal_discovery.instance.Instance;

/**
 * Created by samuelkolb on 16/11/15.
 *
 * @author Samuel Kolb
 */
public interface InstanceBias {

	/**
	 * Returns whether this bias enables the test instance
	 * @param current		The current instance
	 * @param inBody		whether the current instance is a body atom
	 * @param testInstance	The instance to test
	 * @param testInBody	Whether the test instance is a body atom
	 * @return	True iff this bias enables the test instance
	 */
	boolean enables(Instance current, boolean inBody, Instance testInstance, boolean testInBody);

	/**
	 * Returns whether this bias disables the test instance
	 * @param current		The current instance
	 * @param inBody		whether the current instance is a body atom
	 * @param testInstance	The instance to test
	 * @param testInBody	Whether the test instance is a body atom
	 * @return	True iff this bias disables the test instance
	 */
	boolean disables(Instance current, boolean inBody, Instance testInstance, boolean testInBody);

	/**
	 * Combines this bias with another bias.
	 * The combined bias enables/disables instances if this bias or the given bias enable/disable them respectively.
	 * @param bias	The other bias
	 * @return	A new bias
	 */
	default InstanceBias combineWith(InstanceBias bias) {
		InstanceBias original = this;
		return new InstanceBias() {

			@Override
			public boolean enables(Instance current, boolean inBody, Instance testInstance, boolean testInBody) {
				return original.enables(current, inBody, testInstance, testInBody)
						|| bias.enables(current, inBody, testInstance, testInBody);
			}

			@Override
			public boolean disables(Instance current, boolean inBody, Instance testInstance, boolean testInBody) {
				return original.disables(current, inBody, testInstance, testInBody)
						|| bias.disables(current, inBody, testInstance, testInBody);
			}
		};
	}
}
