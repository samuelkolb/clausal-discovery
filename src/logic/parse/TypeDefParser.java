package logic.parse;

import util.parse.MatchParser;
import util.parse.ParsingError;

/**
 * Created by samuelkolb on 24/02/15.
 *
 * @author Samuel Kolb
 */
public class TypeDefParser extends MatchParser<LogicParserState> {

	@Override
	public boolean matches(String string, LogicParserState parseState) throws ParsingError {
		if(!string.matches("type.*\\n"))
			return false;
		String[] parts = string.split("\\s+");
		if(parts.length != 2)
			throw new ParsingError("Type declaration expects one argument");
		String typeName = parts[1];
		if(parseState.containsType(typeName))
			throw new ParsingError("Type '" + typeName + "' already exists");
		parseState.addType(typeName);
		return true;
	}
}
