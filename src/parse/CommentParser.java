package parse;

import java.util.Arrays;
import java.util.List;

/**
 * Created by samuelkolb on 10/03/15.
 *
 * @author Samuel Kolb
 */
public class CommentParser extends ScopeParser<LogicParserState> {

	@Override
	public List<ScopeParser<LogicParserState>> getParsers() {
		return Arrays.asList(new MatchParser<LogicParserState>() {
			@Override
			public boolean matches(String string, LogicParserState parseState) throws ParsingError {
				return !string.equals("\n");
			}
		});
	}

	@Override
	public boolean activatesWith(String string, LogicParserState parseState) throws ParsingError {
		return string.matches("\\s*(//|#)");
	}

	@Override
	public boolean endsWith(String string, LogicParserState parseState) throws ParsingError {
		return string.equals("\n");
	}
}
