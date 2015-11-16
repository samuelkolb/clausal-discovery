package parse;

import java.util.Arrays;
import java.util.List;

/**
 * Created by samuelkolb on 24/02/15.
 *
 * @author Samuel Kolb
 */
public class ExampleParser extends ScopeParser<LogicParserState> {

	//region Variables
	private final List<ScopeParser<LogicParserState>> parser = Arrays.asList(
			new PredicateParser(),
			new ConstDefParser(),
			new LineRemover()
	);

	private String name = null;
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
		if(!string.matches("example\\s+\\w+\\s+([+-]\\s+)?\\{\\s*\\n"))
			return false;
		parseState.resetExample();
		String[] parts = string.split("\\s+");
		this.name = parts[1];
		if(parts[2].equals("+"))
			parseState.setPositiveExample(true);
		else if(parts[2].equals("-"))
			parseState.setPositiveExample(false);
		return true;
	}

	@Override
	public boolean endsWith(String string, LogicParserState parseState) throws ParsingError {
		if(!string.endsWith("}"))
			return false;
		parseState.addExample(this.name);
		return true;
	}

	//endregion
}
