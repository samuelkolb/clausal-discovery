package logic.theory;

import vector.SafeList;
import vector.Vector;

/**
 * A generic logic program consists of a vocabulary, a theory and multiple structures
 *
 * @author Samuel Kolb
 */
public class KnowledgeBase {

	private final Vocabulary vocabulary;

	public Vocabulary getVocabulary() {
		return vocabulary;
	}

	private final SafeList<Theory> theories;

	public SafeList<Theory> getTheories() {
		return theories;
	}

	private final SafeList<Theory> backgroundTheories;

	public SafeList<Theory> getBackgroundTheories() {
		return backgroundTheories;
	}

	private final SafeList<Structure> structures;

	public SafeList<Structure> getStructures() {
		return structures;
	}

	/**
	 * Creates a new logic program without background theories
	 * @param vocabulary    The type and predicate definitions
	 * @param theories        The theories
	 * @param structures    The structures
	 */
	public KnowledgeBase(Vocabulary vocabulary, SafeList<Theory> theories, SafeList<Structure> structures) {
		this(vocabulary, theories, new SafeList<>(), structures);
	}

	/**
	 * Creates a new logic program
	 * @param vocabulary			The type and predicate definitions
	 * @param theories				The theories
	 * @param backgroundTheories	The background theories
	 * @param structures			The structures
	 */
	public KnowledgeBase(Vocabulary vocabulary, SafeList<Theory> theories,
						 SafeList<Theory> backgroundTheories, SafeList<Structure> structures) {
		this.vocabulary = vocabulary;
		this.theories = theories;
		this.backgroundTheories = backgroundTheories;
		this.structures = structures;
	}
}
