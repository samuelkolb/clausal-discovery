package clausal_discovery.core;

import clausal_discovery.validity.ValidityTable;
import log.Log;
import logic.example.Example;
import vector.Vector;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The preferences classes contains preferences and can produce modified preferences as well as output for SVM rank.
 *
 * @author Samuel Kolb
 */
public class Preferences {

	private static class Group {

		// IVAR ordering - The descending ordering of the members of this group

		private final List<List<Example>> ordering;

		public List<List<Example>> getOrdering() {
			return ordering;
		}

		private Group(List<List<Example>> ordering) {
			this.ordering = new ArrayList<>(ordering);
		}

		public Group disturb() {
			List<List<Example>> newOrdering = new ArrayList<>(getOrdering());
			//Collections.shuffle(newOrdering); // TODO Ignores equalities
			Collections.reverse(newOrdering);
			return new Group(newOrdering);
		}
	}

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
			group.getOrdering().forEach(elements::addAll);
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
	public static Preferences newFromOrders(List<List<List<Example>>> orders) {
		return new Preferences(new Vector<>(Group.class, orders.stream().map(Group::new).collect(Collectors.toList())));
	}

	//endregion

	//region Public methods

	/**
	 * Returns the c value to be used in ranking
	 * @param cFactor	The normalised c-factor
	 * @return	A double
	 */
	public double getCValue(double cFactor) {
		return getGroups().size() * cFactor;
	}

	/**
	 * Build a string representation of the orderings
	 * @param validity    A mapping between examples and validity values
	 * @return	A string
	 */
	public String printOrderings(ValidityTable validity) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < getGroups().size(); i++) {
			List<List<Example>> ordering = getGroups().get(i).getOrdering();
			for(int j = 0; j < ordering.size(); j++) {
				for(Example example : ordering.get(j)) {
					builder.append(ordering.size() - j).append(" qid:").append(i + 1);
					for(int c = 0; c < validity.getValidity(example).size(); c++)
						builder.append(String.format(" %d:%d", c + 1, validity.getValidity(example).get(c) ? 1 : 0));
					builder.append(" #\n");
				}
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
		int newSize = (int) Math.ceil(groups.size() * factor);
		Log.LOG.formatLine("Resize from %d preferences to %d preferences", getGroups().size(), newSize);
		return new Preferences(new Vector<>(Group.class, groups.subList(0, newSize)));
	}

	/**
	 * Returns a new preferences object where a random subset of size old-size * factor has been reversed
	 * @param factor	A double between 0 and 1
	 * @return	A new preferences object
	 */
	public Preferences induceNoise(double factor) {
		if(factor < 0 || factor > 1)
			throw new IllegalArgumentException("Illegal factor: " + factor);
		Log.LOG.printLine("Shuffle");
		List<Group> groups = new ArrayList<>(getGroups());
		Collections.shuffle(groups);
		Log.LOG.printLine("Noise: " + (int) (groups.size() * factor) + " of " + groups.size());
		for(int i = 0; i < (int) (groups.size() * factor); i++)
			groups.set(i, groups.get(i).disturb());
		return new Preferences(new Vector<>(Group.class, groups));
	}

	//endregion
}
