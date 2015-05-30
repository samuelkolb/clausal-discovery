package parse;

import log.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Created by samuelkolb on 10/03/15.
 *
 * @author Samuel Kolb
 */
public class CommentParser extends ScopeParser<LogicParserState> {

	private boolean line;

	@Override
	public List<ScopeParser<LogicParserState>> getParsers() {
		return Arrays.asList(new MatchParser<LogicParserState>() {
			@Override
			public boolean matches(String string, LogicParserState parseState) throws ParsingError {
				return !string.equals(line ? "\n" : "%");
			}
		});
	}

	@Override
	public boolean activatesWith(String string, LogicParserState parseState) throws ParsingError {
		if(string.matches("\\s*(//|#)"))
			line = true;
		else if(string.matches("\\s*%"))
			line = false;
		else
			return false;
		return true;
	}

	@Override
	public boolean endsWith(String string, LogicParserState parseState) throws ParsingError {
		return string.equals(line ? "\n" : "%");
	}
}
