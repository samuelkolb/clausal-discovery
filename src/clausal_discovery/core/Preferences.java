package clausal_discovery.core;

import clausal_discovery.validity.ValidityTable;
import log.Log;
import logic.example.Example;
import vector.Vector;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by samuelkolb on 27/04/15.
 *
 * @author Samuel Kolb
 */
public class Preferences {

	private static class Group {

		// IVAR ordering - The descending ordering of the members of this group

		private final Vector<Example> ordering;

		public Vector<Example> getOrdering() {
			return ordering;
		}

		private Group(Vector<Example> ordering) {
			this.ordering = ordering;
		}
	}

	public static final double C_FACTOR = 0.001;

	//region Variables

	// IVAR groups - A vector of groups

	private final Vector<Group> groups;

	private List<Group> getGroups() {
		return new ArrayList<>(groups);
	}

	private final Vector<Example> elements;

	public List<Example> getElements() {
		return new ArrayList<>(elements);
	}

	//endregion

	//region Construction

	private Preferences(Vector<Group> groups) {
		this.groups = groups;
		Set<Example> elements = new HashSet<>();
		for(Group group : groups)
			elements.addAll(group.getOrdering().stream().collect(Collectors.toList()));
		this.elements = new Vector<>(Example.class, elements);
	}

	/**
	 * Returns the number of preferences
	 * @return	An int
	 */
	public int size() {
		return getGroups().size();
	}

	/**
	 * Creates a preference object from a list of vectors containing orderings (descending)
	 * @param orders	A list of vector. Each vector contains a number of integers that represent a single preference.
	 * @return	A preferences object
	 */
	public static Preferences newFromOrders(List<Vector<Example>> orders) {
		return new Preferences(new Vector<>(Group.class, orders.stream().map(Group::new).collect(Collectors.toList())));
	}

	//endregion

	//region Public methods

	/**
	 * Returns the c value to be used in ranking
	 * @return	A double
	 */
	public double getCValue() {
		return getGroups().size() * C_FACTOR;
	}

	/**
	 * Build a string representation of the orderings
	 * @param validity    A mapping between examples and validity values
	 * @return	A string
	 */
	public String printOrderings(ValidityTable validity) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < getGroups().size(); i++) {
			Vector<Example> ordering = getGroups().get(i).getOrdering();
			for(int j = 0; j < ordering.size(); j++) {
				builder.append(ordering.length - j).append(" qid:").append(i + 1);
				for(int c = 0; c < validity.getValidity(ordering.get(j)).size(); c++)
					builder.append(" ").append(c + 1).append(":").append(validity.getValidity(ordering.get(j)).get(c) ? 1 : 0);
				builder.append(" #\n");
			}
		}
		return builder.toString();
	}

	/**
	 * Returns a new preferences object that contains a random subset of size old-size * factor
	 * @param factor	A double between 0 and 1
	 * @return	A new preferences object
	 */
	public Preferences resize(double factor) {
		if(factor < 0 || factor > 1)
			throw new IllegalArgumentException("Illegal factor: " + factor);
		List<Group> groups = new ArrayList<>(getGroups());
		Collections.shuffle(groups);
		return new Preferences(new Vector<>(Group.class, groups.subList(0, (int) (groups.size() * factor))));
	}

	/**
	 * Returns a new preferences object where a random subset of size old-size * factor has been reversed
	 * @param factor	A double between 0 and 1
	 * @return	A new preferences object
	 */
	public Preferences induceNoise(double factor) {
		if(factor < 0 || factor > 1)
			throw new IllegalArgumentException("Illegal factor: " + factor);
		List<Group> groups = new ArrayList<>(getGroups());
		Collections.shuffle(groups);
		Log.LOG.printLine("Noise: " + (int) (groups.size() * factor) + " of " + groups.size());
		for(int i = 0; i < (int) (groups.size() * factor); i++)
			groups.set(i, new Group(groups.get(i).getOrdering().reverse()));
		return new Preferences(new Vector<>(Group.class, groups));
	}

	//endregion
}
