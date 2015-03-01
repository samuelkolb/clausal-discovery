package util.parse;

/**
 * Created by samuelkolb on 24/02/15.
 *
 * @author Samuel Kolb
 */
public class ParseCursor {

	//region Variables
	private int line = 0;

	private int column = 0;

	private final String[] lines;
	//endregion

	//region Construction

	/**
	 * Creates a new parse cursor with the given content
	 * @param program	The full program
	 */
	public ParseCursor(String program) {
		this(program.split("\n"));
	}

	/**
	 * Creates a new parse cursor with the given content
	 * @param lines	The lines of the input program
	 */
	public ParseCursor(String[] lines) {
		this.lines = lines;
	}

	//endregion

	//region Public methods
	public void seek(int line) {
		seek(line, 0);
	}

	public void seek(int line, int column) {
		this.line = line;
		this.column = column;
	}

	public char read() {
		if(column == lines[line].length()) {
			seek(line + 1);
			return '\n';
		}
		return lines[line].charAt(column++);
	}

	public ParseException getException(String message, Throwable cause) {
		return new ParseException(line + 1, message, cause);
	}

	public boolean hasNext() {
		return line < lines.length - 1 || (line == lines.length - 1 && column < lines[line].length());
	}
	//endregion
}
