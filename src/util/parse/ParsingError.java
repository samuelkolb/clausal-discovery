package util.parse;

/**
 * Indicates that a parsing error occurred
 *
 * @author Samuel Kolb
 */
public class ParsingError extends Exception {

	/**
	 * Creates a new ParsingError that will contain the given message
	 * @param message	The error message
	 */
	public ParsingError(String message) {
		super(message);
	}
}
