package clausal_discovery.core.bias;

import clausal_discovery.core.Literal;

/**
 * Expresses a bias between two literals.
 *
 * @author Samuel Kolb
 */
public interface LiteralBias {

	/**
	 * Returns whether this bias enables the test instance
	 * @param current	The current literal
	 * @param test		The tested literal
	 * @return	True iff this bias enables the test instance
	 */
	boolean enables(Literal current, Literal test);

	/**
	 * Returns whether this bias disables the test instance
	 * @param current	The current literal
	 * @param test		The tested literal
	 * @return	True iff this bias disables the test instance
	 */
	boolean disables(Literal current, Literal test);

	/**
	 * Combines this bias with another bias.
	 * The combined bias enables/disables instances if this bias or the given bias enable/disable them respectively.
	 * @param bias	The other bias
	 * @return	A new bias
	 */
	default LiteralBias combineWith(LiteralBias bias) {
		LiteralBias original = this;
		return new LiteralBias() {

			@Override
			public boolean enables(Literal current, Literal test) {
				return original.enables(current, test) || bias.enables(current, test);
			}

			@Override
			public boolean disables(Literal current, Literal test) {
				return original.disables(current, test) || bias.disables(current, test);
			}
		};
	}

	/**
	 * Restricts a bias to be only enable or disable instances in the head or body.
	 * @param enableBody	True if enabling should be restricted to the body of the clause, false for the head
	 * @param disableBody	True if disabling should be restricted to the body of the clause, false for the head
	 * @return	A new bias
	 */
	default LiteralBias restrict(boolean enableBody, boolean disableBody) {
		LiteralBias original = this;
		return new LiteralBias() {
			@Override
			public boolean enables(Literal current, Literal test) {
				return test.isPositive() != enableBody && original.enables(current, test);
			}

			@Override
			public boolean disables(Literal current, Literal test) {
				return test.isPositive() != disableBody && original.enables(current, test);
			}
		};
	}
}
