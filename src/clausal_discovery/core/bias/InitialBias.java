package clausal_discovery.core.bias;

import clausal_discovery.core.Literal;
import clausal_discovery.instance.Instance;

/**
 * Represents a bias expressing how instances enable or disable other instances.
 *
 * @author Samuel Kolb
 */
public interface InitialBias {

	/**
	 * Returns whether this bias enables the test instance
	 * @param test	The tested literal
	 * @return	True iff this bias enables the test instance
	 */
	boolean enables(Literal test);

	/**
	 * Returns whether this bias disables the test instance
	 * @param test	The tested literal
	 * @return	True iff this bias disables the test instance
	 */
	boolean disables(Literal test);

	/**
	 * Combines this bias with another bias.
	 * The combined bias enables/disables instances if this bias or the given bias enable/disable them respectively.
	 * @param initialBias	The other bias
	 * @return	A new bias
	 */
	default InitialBias combineWith(InitialBias initialBias) {
		InitialBias original = this;
		return new InitialBias() {

			@Override
			public boolean enables(Literal test) {
				return original.enables(test) || initialBias.enables(test);
			}

			@Override
			public boolean disables(Literal test) {
				return original.disables(test) || initialBias.disables(test);
			}
		};
	}
}
