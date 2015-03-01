package util.parse;

import java.util.List;

/**
 * Created by samuelkolb on 24/02/15.
 *
 * @author Samuel Kolb
 */
public abstract class ScopeParser<T> {

	private ParseCursor cursor;

	private T parseState;

	/**
	 * Parses using the given cursor and state
	 * @param parseCursor	The parse cursor containing the program and current position
	 * @param parseState	The parse state containing information about the current state
	 * @return	The resulting parse state
	 */
	public T parse(ParseCursor parseCursor, T parseState) {
		this.cursor = parseCursor;
		this.parseState = parseState;
		StringBuilder buffer = new StringBuilder();
		try {
			while (cursor.hasNext()) {
				if (endsWith(buffer.toString(), parseState))
					return parseState;
				buffer.append(cursor.read());
				checkBuffer(buffer);
			}
			endsWith(buffer.toString(), parseState);
		} catch (ParsingError error) {
			throw cursor.getException(error.getMessage(), error);
		}
		return parseState;
	}

	private void checkBuffer(StringBuilder buffer) throws ParsingError {
		String string = buffer.toString();
		for(ScopeParser<T> parser : getParsers()) {
			if(parser.activatesWith(string, parseState)) {
				this.parseState = parser.parse(cursor, parseState);
				buffer.setLength(0);
				return;
			}
		}
	}

	public abstract List<ScopeParser<T>> getParsers();

	/**
	 * Indicates whether or not this parser will activate given a certain string and parse state
	 * @param string		The current buffer content
	 * @param parseState	The current state of the parser
	 * @return	True iff this parser will take over parsing
	 * @throws util.parse.ParsingError	If an error detected during the activation check
	 */
	public abstract boolean activatesWith(String string, T parseState) throws ParsingError;

	/**
	 * Indicates whether or not this parser will end given a certain string and parse state
	 * @param string		The current buffer content
	 * @param parseState	The current state of the parser
	 * @return	True iff this parser will stop parsing
	 */
	public abstract boolean endsWith(String string, T parseState) throws ParsingError;
}
