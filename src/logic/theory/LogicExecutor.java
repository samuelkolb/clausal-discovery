package logic.theory;

import vector.Vector;

/**
 * The logic executor abstracts the necessary (external) logical operations from the system in use
 *
 * @author Samuel Kolb
 */
public interface LogicExecutor {

	/**
	 * Returns whether the given program is valid, a.k.a. has a model (requires exactly one theory)
	 * @param knowledgeBase	The knowledge base to test
	 * @return	True iff the given program is valid
	 */
	@Deprecated
	default boolean testValidityTheory(KnowledgeBase knowledgeBase) {
		if(knowledgeBase.getTheories().size() != 1)
			throw new IllegalArgumentException("Requires exactly one theory");
		return testValidityTheories(knowledgeBase).get(0);
	}

	/**
	 * Returns whether the theories in the given program are valid, a.k.a. have a model
	 * @param knowledgeBase    The knowledge base to test
	 * @return	An array containing the truth value of the validity test per theory
	 */
	Vector<Boolean> testValidityTheories(KnowledgeBase knowledgeBase);

	/**
	 * Returns whether the given program entails the given clause
	 * @param program	The program containing the theory
	 * @param theory	The theory that should be tested
	 * @return	True iff the given program entails the given clause
	 */
	boolean entails(KnowledgeBase program, InlineTheory theory);
}
