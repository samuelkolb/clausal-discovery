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

	@Deprecated
	public Theory getTheory() {
		throw new UnsupportedOperationException();
	}

	public Vector<Theory> getTheories() {
		return theories;
	}

	private final Vector<Structure> structures;

	public Vector<Structure> getStructures() {
		return structures;
	}

	/**
	 * Creates a new logic program
	 * @param vocabulary	The type and predicate definitions
	 * @param theories		The theories
	 * @param structures	The structures
	 */
	public KnowledgeBase(Vocabulary vocabulary, Vector<Theory> theories, Vector<Structure> structures) {
		this.vocabulary = vocabulary;
		this.theories = theories;
		this.structures = structures;
	}
}
