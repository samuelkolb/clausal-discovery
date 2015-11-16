package parse;

import logic.bias.EnumType;

import java.util.Arrays;

/**
 * Created by samuelkolb on 24/02/15.
 *
 * @author Samuel Kolb
 */
public class EnumParser extends MatchParser<LogicParserState> {

	@Override
	public boolean matches(String string, LogicParserState parseState) throws ParsingError {
		if(!string.matches("enum.*\\n"))
			return false;
		String[] parts = string.split("\\s+");
		if(parts.length < 3)
			throw new ParsingError("Enum declaration expects at least three argument");
		String enumName = parts[1];
		if(parseState.containsType(enumName))
			throw new ParsingError("Type '" + enumName + "' already exists");
		parseState.addEnum(new EnumType(enumName, Arrays.asList(parts).subList(2, parts.length)));
		return true;
	}
}
