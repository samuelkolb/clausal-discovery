package parse;

/**
 * Created by samuelkolb on 10/03/15.
 *
 * @author Samuel Kolb
 */
public class SearchParser extends MatchParser<LogicParserState> {

	@Override
	public boolean matches(String string, LogicParserState parseState) throws ParsingError {
		if(!string.matches("search.*\\n"))
			return false;
		String[] parts = string.trim().split("\\s+");
		if(parts.length < 2)
			throw new ParsingError("Search declaration requires at least 1 argument");
		for(int i = 1; i < parts.length; i++) {
			String predicateName = parts[i].trim();
			if (!parseState.containsPredicate(predicateName))
				throw new ParsingError("Use of unknown predicate '" + predicateName + "'");
			parseState.addSearchPredicate(predicateName);
		}
		return true;

	}
}
