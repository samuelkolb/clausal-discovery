package clausal_discovery.core.bias;

import clausal_discovery.core.Literal;
import clausal_discovery.instance.Instance;

/**
 * Represents the connection bias, that two clauses must be connected.
 *
 * @author Samuel Kolb
 */
public class ConnectedLiteralBias implements LiteralBias {

	@Override
	public boolean enables(Literal current, Literal test) {
		if(test.getVariables().isEmpty()) {
			// The test instance has no variables
			return true;
		} else if(current.getVariables().isEmpty()) {
			// The current instance has no variables, but the test instance does
			return false;
		}
		for(int c : current.getVariables()) {
			for(int t : test.getVariables()) {
				if(c == t) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean disables(Literal current, Literal test) {
		return false;
	}
}
