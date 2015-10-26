package parse;

import util.Pair;

/**
 * Created by samuelkolb on 24/02/15.
 *
 * @author Samuel Kolb
 */
public class PredicateParser extends MatchParser<LogicParserState> {

	//region Variables

	//endregion

	//region Construction

	//endregion

	//region Public methods

	@Override
	public boolean matches(String string, LogicParserState parseState) throws ParsingError {
		if(!string.matches("\\s+[a-zA-Z_]+\\(.*\\)"))
			return false;
		Pair<String, String[]> predicate = parseState.parsePredicate(string.trim());
		if(!parseState.containsPredicate(predicate.getFirst()))
			throw new ParsingError("Use of unknown predicate '" + predicate.getFirst() + "'");
		for(String arg : predicate.getSecond())
		if(!parseState.containsConstant(arg))
			throw new ParsingError("Use of unknown constant '" + arg + "'");
		parseState.addInstance(predicate.getFirst(), predicate.getSecond());
		return true;
	}


	//endregion
}
