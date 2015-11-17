package clausal_discovery.core.bias;

import clausal_discovery.instance.Instance;

/**
 * Represents a bias expressing how instances enable or disable other instances.
 *
 * @author Samuel Kolb
 */
public interface Bias {

	/**
	 * Returns whether this bias enables the test instance
	 * @param testInstance	The instance to test
	 * @param testInBody	Whether the test instance is a body atom
	 * @return	True iff this bias enables the test instance
	 */
	boolean enables(Instance testInstance, boolean testInBody);

	/**
	 * Returns whether this bias disables the test instance
	 * @param testInstance	The instance to test
	 * @param testInBody	Whether the test instance is a body atom
	 * @return	True iff this bias disables the test instance
	 */
	boolean disables(Instance testInstance, boolean testInBody);

	/**
	 * Combines this bias with another bias.
	 * The combined bias enables/disables instances if this bias or the given bias enable/disable them respectively.
	 * @param bias	The other bias
	 * @return	A new bias
	 */
	default Bias combineWith(Bias bias) {
		Bias original = this;
		return new Bias() {

			@Override
			public boolean enables(Instance testInstance, boolean testInBody) {
				return original.enables(testInstance, testInBody) || bias.enables(testInstance, testInBody);
			}

			@Override
			public boolean disables(Instance testInstance, boolean testInBody) {
				return original.disables(testInstance, testInBody) || bias.disables(testInstance, testInBody);
			}
		};
	}
}
