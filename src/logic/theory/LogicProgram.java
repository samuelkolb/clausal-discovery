package logic.theory;

import vector.Vector;

/**
 * A generic logic program consists of a vocabulary, a theory and multiple structures
 */
public class LogicProgram {

	private final Vocabulary vocabulary;

	public Vocabulary getVocabulary() {
		return vocabulary;
	}

	private final Theory theory;

	public Theory getTheory() {
		return theory;
	}

	private final Vector<Structure> structures;

	public Vector<Structure> getStructures() {
		return structures;
	}

	/**
	 * Creates a new logic program
	 * @param vocabulary	The type and predicate definitions
	 * @param theory		The theory
	 * @param structures	The structures
	 */
	public LogicProgram(Vocabulary vocabulary, Theory theory, Vector<Structure> structures) {
		this.vocabulary = vocabulary;
		this.theory = theory;
		this.structures = structures;
	}
}
