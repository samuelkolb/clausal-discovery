package util.parse;

/**
* Created by samuelkolb on 24/02/15.
*
* @author Samuel Kolb
*/
public class ParseException extends RuntimeException {

	public ParseException() {
	}

	public ParseException(int line) {
		super(getMessage(line, null));
	}

	public ParseException(int line, String message) {
		super(getMessage(line, message));
	}

	public ParseException(int line, String message, Throwable cause) {
		super(getMessage(line, message), cause);
	}

	public ParseException(int line, Throwable cause) {
		super(getMessage(line, null), cause);
	}

	public ParseException(int line, String message, Throwable cause, boolean enableSuppression,
						  boolean writableStackTrace) {
		super(getMessage(line, message), cause, enableSuppression, writableStackTrace);
	}

	private static String getMessage(int line, String message) {
		String error = "Error on line " + line;
		return message != null ? error + ": " + message : error;
	}
}
