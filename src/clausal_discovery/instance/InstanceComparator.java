package clausal_discovery.instance;

import java.util.Comparator;

/**
 * Created by samuelkolb on 12/04/15.
 *
 * @author Samuel Kolb
 */
public class InstanceComparator implements Comparator<PositionedInstance> {

	@Override
	public int compare(PositionedInstance o1, PositionedInstance o2) {
		int body = o1.isInBody() == o2.isInBody() ? 0 : (o1.isInBody() ? -1 : 1);
		return body != 0 ? body : Integer.compare(o1.getIndex(), o2.getIndex());
	}
}
