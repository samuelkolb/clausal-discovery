package clausal_discovery.core;

import clausal_discovery.instance.InstanceList;

/**
 * Efficiently represents an ordered set of literals (a clause)
 *
 * @author Samuel Kolb
 */
class LiteralSet implements Comparable<LiteralSet> {

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

	private LiteralSet(AtomSet body, AtomSet head) {
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

	public int size() {
		return body.size() + head.size();
	}

	public boolean isEmpty() {
		return body.isEmpty() && head.isEmpty();
	}

	public boolean isInBody() {
		return head.isEmpty();
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
}
