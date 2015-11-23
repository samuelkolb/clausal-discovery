package clausal_discovery.core.bias;

import clausal_discovery.core.Literal;
import clausal_discovery.instance.Instance;
import log.Log;

/**
 * Created by samuelkolb on 16/11/15.
 *
 * @author Samuel Kolb
 */
public class OrderedLiteralBias implements LiteralBias, InitialBias {

	@Override
	public boolean enables(Literal test) {
		return isOrdered(test, -1);
	}

	@Override
	public boolean disables(Literal test) {
		return false;
	}

	@Override
	public boolean enables(Literal current, Literal test) {
		return isOrdered(test, current.getRank());
	}

	@Override
	public boolean disables(Literal current, Literal test) {
		return false;
	}

	protected boolean isOrdered(Literal test, int min) {
		for(int i : test.getVariables()) {
			if(i == min + 1) {
				min++;
			} else if(i > min) {
				return false;
			}
		}
		return true;
	}
}
