package logic.parse;

import util.parse.ParsingError;
import util.parse.ScopeParser;

import java.util.Arrays;
import java.util.List;

/**
 * Created by samuelkolb on 24/02/15.
 *
 * @author Samuel Kolb
 */
public class ExampleParser extends ScopeParser<LogicParserState> {

	//region Variables
	private List<ScopeParser<LogicParserState>> parser =
			Arrays.asList((ScopeParser<LogicParserState>)new PredicateParser());

	private String name;
	//endregion

	//region Construction

	//endregion

	//region Public methods

	@Override
	public List<ScopeParser<LogicParserState>> getParsers() {
		return parser;
	}

	@Override
	public boolean activatesWith(String string, LogicParserState parseState) throws ParsingError {
		if(!string.matches("example\\s+[A-Za-z0-9]+\\s+\\{\\s*\\n"))
			return false;
		String[] parts = string.split("\\s+", 3);
		this.name = parts[1];
		if(parseState.containsExample(name))
			throw new ParsingError("Example '" + name + "' already exists");
		return true;
	}

	@Override
	public boolean endsWith(String string, LogicParserState parseState) throws ParsingError {
		if(!string.endsWith("}"))
			return false;
		if(parseState.containsExample(name))
			throw new ParsingError("Example '" + name + "' has already been added");
		parseState.addExample(name);
		return true;
	}

	//endregion
}
