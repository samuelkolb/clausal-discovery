package parse;

import basic.FileUtil;
import clausal_discovery.core.LogicBase;

import java.io.File;
import java.util.Arrays;

/**
 * The logic parser parses logic files
 *
 * @author Samuel Kolb
 */
public class LogicParser {

	/**
	 * Read and parse the local file with the given name. (Local files reside under res/examples/)
	 * @param name	The name of the file (including the extension)
	 * @return	The LogicBase parsed from the file
	 */
	public LogicBase readLocalFile(String name) {
		File file = FileUtil.getLocalFile(getClass().getResource("/examples/" + name));
		String program = FileUtil.readFile(file);

		LogicParserState state = new BaseScopeParser<>(Arrays.asList(
			new CommentParser(),
			new TypeDefParser(),
			new PredicateDefParser(),
			new ExampleParser(),
			new SearchParser(),
			new LineRemover()
		)).parse(new ParseCursor(program), new LogicParserState());

		return state.getLogicBase();
	}
}
