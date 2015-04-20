package logic.theory;

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

	private final Vector<Theory> theories;

	public Vector<Theory> getTheories() {
		return theories;
	}

	private final Vector<Theory> backgroundTheories;

	public Vector<Theory> getBackgroundTheories() {
		return backgroundTheories;
	}

	private final Vector<Structure> structures;

	public Vector<Structure> getStructures() {
		return structures;
	}

	/**
	 * Creates a new logic program without background theories
	 * @param vocabulary	The type and predicate definitions
	 * @param theories		The theories
	 * @param structures	The structures
	 */
	public KnowledgeBase(Vocabulary vocabulary, Vector<Theory> theories, Vector<Structure> structures) {
		this(vocabulary, theories, new Vector<>(), structures);
	}

	/**
	 * Creates a new logic program
	 * @param vocabulary			The type and predicate definitions
	 * @param theories				The theories
	 * @param backgroundTheories	The background theories
	 * @param structures			The structures
	 */
	public KnowledgeBase(Vocabulary vocabulary, Vector<Theory> theories,
						 Vector<Theory> backgroundTheories, Vector<Structure> structures) {
		this.vocabulary = vocabulary;
		this.theories = theories;
		this.backgroundTheories = backgroundTheories;
		this.structures = structures;
	}
}
