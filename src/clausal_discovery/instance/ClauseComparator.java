package clausal_discovery.instance;

import util.Numbers;

import java.util.Comparator;

/**
* Created by samuelkolb on 12/04/15.
*
* @author Samuel Kolb
*/
class ClauseComparator implements Comparator<Numbers.Permutation> {

	@Override
	public int compare(Numbers.Permutation o1, Numbers.Permutation o2) {
		int index = 0;
		while(o1.getArray().length > index && o2.getArray().length > index) {
			if(o1.getArray()[index] > o2.getArray()[index])
				return 1;
			else if(o1.getArray()[index] < o2.getArray()[index])
				return -1;
			index++;
		}
		return Integer.compare(o1.getArray().length, o2.getArray().length);
	}
}
