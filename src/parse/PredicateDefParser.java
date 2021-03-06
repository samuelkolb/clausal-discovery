package parse;

import pair.Pair;

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
		if(!string.matches("(pred|symm|calc)\\s+.*\\n"))
			return false;
		String[] parts = string.split("\\s+", 2);
		if(parts.length < 2)
			throw new ParsingError("Predicate declaration expects one argument");
		readPredicateDefinition(parts[1], parts[0], parseState);
		return true;
	}

	private void readPredicateDefinition(String string, String type, LogicParserState state) throws ParsingError {
		Pair<String, String[]> predicate;
		try {
			predicate = state.parsePredicate(string);
		} catch(IllegalArgumentException e) {
			throw new ParsingError("Badly formatted predicate declaration: " + string);
		}
		if(state.containsPredicate(predicate.getFirst()))
			throw new ParsingError("Predicate '" + predicate.getFirst() + "' already exists");
		for(String arg : predicate.getSecond())
			if(!state.containsType(arg))
				throw new ParsingError("Use of unknown type '" + arg + "'");
		try {
			state.addPredicate(predicate.getFirst(), type.equals("symm"), type.equals("calc"), predicate.getSecond());
		} catch(IllegalArgumentException e) {
			throw new ParsingError("Could not add predicate definition because: " + e.getMessage());
		}
	}
	//endregion
}
