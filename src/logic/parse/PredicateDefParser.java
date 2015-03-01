package logic.parse;

import log.Log;
import util.Pair;
import parse.MatchParser;
import parse.ParsingError;

/**
 * Created by samuelkolb on 24/02/15.
 *
 * @author Samuel Kolb
 */
public class PredicateDefParser extends MatchParser<LogicParserState> {

	//region Variables

	//endregion

	//region Construction

	//endregion

	//region Public methods

	@Override
	public boolean matches(String string, LogicParserState parseState) throws ParsingError {
		if(!string.matches("pred.*\\n"))
			return false;
		String[] parts = string.split("\\s+", 2);
		if(parts.length < 2)
			throw new ParsingError("Predicate declaration expects one argument");
		readPredicateDefinition(parts[1], parseState);
		return true;
	}

	private void readPredicateDefinition(String string, LogicParserState state) throws ParsingError {
		Pair<String, String[]> predicate;
		try {
			predicate = state.parsePredicate(string);
		} catch(IllegalArgumentException e) {
			Log.LOG.printLine(string);
			throw new ParsingError("Badly formatted predicate declaration");
		}
		if(state.containsPredicate(predicate.getFirst()))
			throw new ParsingError("Predicate '" + predicate.getFirst() + "' already exists");
		for(String arg : predicate.getSecond())
			if(!state.containsType(arg))
				throw new ParsingError("Use of unknown type '" + arg + "'");
		state.addPredicate(predicate.getFirst(), predicate.getSecond());
	}
	//endregion
}
