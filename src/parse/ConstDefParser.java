package parse;

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
		if(!string.matches("\\s+const.*\\n"))
			return false;
		String[] parts = string.trim().split("\\s+");
		if(parts.length < 3)
			throw new ParsingError("Expected at least 2 arguments for constant, got: " + parts.length);
		String typeString = parts[1].trim();
		if (!parseState.containsType(typeString))
			throw new ParsingError("Use of unknown type '" + typeString + "'");
		for(int i = 2; i < parts.length; i++) {
			String constName = parts[i].trim();
			if (parseState.containsConstant(constName))
				throw new ParsingError("Constant '" + constName + "' already exists");
			parseState.addConstant(constName, typeString);

		}
		return true;
	}

	//endregion
}
