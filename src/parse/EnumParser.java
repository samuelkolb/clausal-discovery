package parse;

/**
 * Created by samuelkolb on 24/02/15.
 *
 * @author Samuel Kolb
 */
public class EnumParser extends MatchParser<LogicParserState> {

	@Override
	public boolean matches(String string, LogicParserState parseState) throws ParsingError {
		// TODO implement
		if(!string.matches("enum.*\\n"))
			return false;
		String[] parts = string.split("\\s+");
		if(parts.length < 2)
			throw new ParsingError("Type declaration expects at least one argument");
		String typeName = parts[1];
		if(parseState.containsType(typeName))
			throw new ParsingError("Type '" + typeName + "' already exists");
		if(parts.length > 2) {
			if(!parts[2].equals(">"))
				throw new ParsingError("Expected inheritance symbol '>'");
			else if(parts.length != 4)
				throw new ParsingError("Expected super type");
			else if(!parseState.containsType(parts[3]))
				throw new ParsingError("Use of unknown type '" + parts[3] + "'");
			parseState.addSubType(parts[3], typeName);
		} else
			parseState.addType(typeName);
		return true;
	}
}
