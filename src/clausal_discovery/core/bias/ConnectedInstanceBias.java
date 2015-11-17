package clausal_discovery.core.bias;

import clausal_discovery.instance.Instance;

/**
 * Represents the connection bias, that two clauses must be connected.
 *
 * @author Samuel Kolb
 */
public class ConnectedInstanceBias implements InstanceBias {

	@Override
	public boolean enables(Instance current, boolean inBody, Instance testInstance, boolean testInBody) {
		if(!testInBody) {
			return false;
		} else if(testInstance.getVariableIndices().isEmpty()) {
			// The test instance has no variables
			return true;
		} else if(current.getVariableIndices().isEmpty()) {
			// The current instance has no variables, but the test instance does
			return false;
		}
		for(int c : current.getVariableIndices()) {
			for(int t : testInstance.getVariableIndices()) {
				if(c == t) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean disables(Instance current, boolean inBody, Instance testInstance, boolean testInBody) {
		return false;
	}
}
