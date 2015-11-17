package clausal_discovery.core.bias;

import clausal_discovery.instance.Instance;

import java.util.Optional;

/**
 * Created by samuelkolb on 16/11/15.
 *
 * @author Samuel Kolb
 */
public class OrderedInstanceBias implements InstanceBias, Bias {

	@Override
	public boolean enables(Instance testInstance, boolean testInBody) {
		return isOrdered(testInstance, 0);
	}

	@Override
	public boolean disables(Instance testInstance, boolean testInBody) {
		return false;
	}

	@Override
	public boolean enables(Instance current, boolean inBody, Instance testInstance, boolean testInBody) {
		if(!testInBody) {
			return false;
		}
		int min = current.getMax();
		return isOrdered(testInstance, min);
	}

	@Override
	public boolean disables(Instance current, boolean inBody, Instance testInstance, boolean testInBody) {
		return false;
	}

	protected boolean isOrdered(Instance testInstance, int min) {
		for(int i : testInstance.getVariableIndices()) {
			if(i == min + 1) {
				min++;
			} else if(i > min) {
				return false;
			}
		}
		return true;
	}
}
