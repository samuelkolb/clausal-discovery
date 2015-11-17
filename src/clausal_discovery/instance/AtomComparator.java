package clausal_discovery.instance;

import clausal_discovery.core.PredicateDefinition;
import vector.SafeList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by samuelkolb on 17/11/15.
 *
 * @author Samuel Kolb
 */
class AtomComparator implements Comparator<Instance> {

	private final SafeList<PredicateDefinition> definitions;

	public AtomComparator(List<PredicateDefinition> definitions) {
		this.definitions = SafeList.from(definitions);
	}

	@Override
	public int compare(Instance o1, Instance o2) {
		List<Integer> list1 = new ArrayList<>(new LinkedHashSet<>(o1.getVariableIndices().sortedCopy()));
		List<Integer> list2 = new ArrayList<>(new LinkedHashSet<>(o2.getVariableIndices().sortedCopy()));
		for(int i = 0; i < Math.max(list1.size(), list2.size()); i++) {
			if(i >= list1.size()) {
				return -1;
			} else if(i >= list2.size()) {
				return 1;
			}
			int v = Integer.compare(list1.get(i), list2.get(i));
			if(v != 0) {
				return v;
			}
		}
		for(int i = 0; i < o1.getVariableIndices().size(); i++) {
			int o = Integer.compare(o1.getVariableIndices().get(i), o2.getVariableIndices().get(i));
			if(o != 0) {
				return o;
			}
		}
		return Integer.compare(definitions.indexOf(o1.getDefinition()), definitions.indexOf(o2.getDefinition()));
	}
}
