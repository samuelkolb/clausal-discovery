package logic.parse;

import basic.FileUtil;
import parse.BaseScopeParser;
import parse.ParseCursor;
import clausal_discovery.LogicBase;

import java.io.File;
import java.util.Arrays;

/**
 * Created by samuelkolb on 22/02/15.
 */
public class LogicParser {

	public LogicBase readLocalFile(String name) {
		File file = FileUtil.getLocalFile(getClass().getResource("/examples/" + name));
		String program = FileUtil.readFile(file);

		LogicParserState state = new BaseScopeParser<>(Arrays.asList(
			new TypeDefParser(),
			new PredicateDefParser(),
			new ExampleParser(),
			new LineRemover()
		)).parse(new ParseCursor(program), new LogicParserState());

		return state.getLogicBase();
	}
}
