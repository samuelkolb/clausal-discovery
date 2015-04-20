package logic.theory;

/**
 * The logic executor abstracts the necessary (external) logical operations from the system in use
 *
 * @author Samuel Kolb
 */
public interface LogicExecutor {

	/**
	 * Returns whether the given program is valid, a.k.a. has a model
	 * @param program	The program to execute
	 * @return	True iff the given program is valid
	 */
	public boolean isValid(KnowledgeBase program);

	boolean[] areValid(KnowledgeBase program);

	/**
	 * Returns whether the given program entails the given clause
	 * @param program	The program containing the theory
	 * @param theory	The theory that should be tested
	 * @return	True iff the given program entails the given clause
	 */
	boolean entails(KnowledgeBase program, InlineTheory theory);
}
