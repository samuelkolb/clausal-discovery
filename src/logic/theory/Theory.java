package logic.theory;

/**
 * A logical theory that consists of a set of formulas
 *
 * @author Samuel Kolb
 */
public interface Theory {

	public static interface Visitor<T> {

		/**
		 * Visit the given inline theory
		 * @param inlineTheory	The theory to visit
		 * @return	The result
		 */
		public T visit(InlineTheory inlineTheory);

		/**
		 * Visit the given file theory
		 * @param fileTheory	The theory to visit
		 * @return	The result
		 */
		public T visit(FileTheory fileTheory);
	}

	/**
	 * Accept the given visitor by calling the appropriate method
	 * @param visitor	The visitor to accept
	 * @return	The result of visiting the visitor
	 */
	public <T> T accept(Visitor<T> visitor);
}
