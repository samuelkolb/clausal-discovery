package logic.parse;

import util.parse.MatchParser;
import util.parse.ParsingError;

/**
 * Created by samuelkolb on 24/02/15.
 *
 * @author Samuel Kolb
 */
public class LineRemover extends MatchParser<LogicParserState> {

	//region Variables

	//endregion

	//region Construction

	//endregion

	//region Public methods

	@Override
	public boolean matches(String string, LogicParserState parseState) throws ParsingError {
		return string.equals("\n");
	}

	//endregion
}
