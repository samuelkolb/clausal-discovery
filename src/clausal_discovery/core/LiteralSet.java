package clausal_discovery.core;

import basic.StringUtil;
import clausal_discovery.instance.InstanceList;

import java.util.ArrayList;
import java.util.List;

/**
 * Efficiently represents an ordered set of literals (a clause)
 *
 * @author Samuel Kolb
 */
public class LiteralSet implements Comparable<LiteralSet> {

	private final AtomSet body;

	public AtomSet getBody() {
		return body;
	}

	private final AtomSet head;

	public AtomSet getHead() {
		return head;
	}

	public LiteralSet(InstanceList list) {
		this.body = new AtomSet(list);
		this.head = new AtomSet(list);
	}

	public LiteralSet(AtomSet body, AtomSet head) {
		if(!body.getInstanceList().equals(head.getInstanceList())) {
			throw new IllegalArgumentException("Both the body and head atom sets must have the same instance list");
		}
		this.body = body;
		this.head = head;
	}

	public LiteralSet add(int index, boolean inBody) {
		if(inBody) {
			AtomSet newSet = new AtomSet(body);
			newSet.add(index);
			return new LiteralSet(newSet, head);
		} else {
			AtomSet newSet = new AtomSet(head);
			newSet.add(index);
			return new LiteralSet(body, newSet);
		}
	}

	public LiteralSet union(LiteralSet literalSet) {
		return new LiteralSet(body.union(literalSet.body), head.union(literalSet.head));
	}

	public LiteralSet intersect(LiteralSet literalSet) {
		return new LiteralSet(body.intersect(literalSet.body), head.intersect(literalSet.head));
	}

	public LiteralSet minus(LiteralSet literalSet) {
		return new LiteralSet(body.minus(literalSet.body), head.minus(literalSet.head));
	}

	public boolean contains(int index, boolean inBody) {
		return (inBody ? getBody() : getHead()).contains(index);
	}

	public int size() {
		return body.size() + head.size();
	}

	public boolean isEmpty() {
		return body.isEmpty() && head.isEmpty();
	}

	public boolean isSubsetOf(LiteralSet literalSet) {
		return getHead().isSubsetOf(literalSet.getHead()) && getBody().isSubsetOf(literalSet.getBody());
	}

	public boolean hasHead() {
		return !head.isEmpty();
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		LiteralSet literalSet = (LiteralSet) o;
		return body.equals(literalSet.body) && head.equals(literalSet.head);

	}

	@Override
	public int hashCode() {
		int result = body.hashCode();
		result = 31 * result + head.hashCode();
		return result;
	}

	@Override
	public int compareTo(LiteralSet o) {
		int bodyCompare = body.compareTo(o.body);
		return bodyCompare == 0 ? head.compareTo(head) : bodyCompare;
	}

	@Override
	public String toString() {
		List<String> list = new ArrayList<>();
		getBody().forEach(i -> list.add("~" + getBody().getInstanceList().get(i)));
		getHead().forEach(i -> list.add("" + getHead().getInstanceList().get(i)));
		return "[" + StringUtil.join(", ", list) + "]";
	}
}
