package logic.parse;

import parse.MatchParser;
import parse.ParsingError;

/**
 * Created by samuelkolb on 24/02/15.
 *
 * @author Samuel Kolb
 */
public class ConstDefParser extends MatchParser<LogicParserState> {

	//region Variables

	//endregion

	//region Construction

	//endregion

	//region Public methods

	@Override
	public boolean matches(String string, LogicParserState parseState) throws ParsingError {
		if(!string.matches("const.*\\n"))
			return false;
		String[] parts = string.split("\\s+");
		if(parts.length != 3)
			throw new ParsingError("Expected 2 arguments for constant, got: " + parts.length);
		String typeString = parts[1].trim();
		String constName = parts[2].trim();
		if (!parseState.containsType(typeString))
			throw new ParsingError("Use of unknown type '" + typeString + "'");
		if (parseState.containsConstant(constName))
			throw new ParsingError("Constant '" + constName + "' already exists");
		parseState.addConstant(constName, typeString);
		return true;
	}

	//endregion
}
