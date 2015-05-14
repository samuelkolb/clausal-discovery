package parse;

import clausal_discovery.core.LogicBase;
import log.Log;
import time.Stopwatch;

import java.util.ArrayList;
import java.util.List;

/**
 * The logic parser parses logic files
 *
 * @author Samuel Kolb
 */
public class LogicParser implements LocalParser<LogicBase> {

	@Override
	public LogicBase parse(String content) {
		Stopwatch stopwatch = new Stopwatch(true);
		List<ScopeParser<LogicParserState>> parsers = new ArrayList<>();
		parsers.add(new CommentParser());
		parsers.add(new TypeDefParser());
		parsers.add(new PredicateDefParser());
		parsers.add(new ExampleParser());
		parsers.add(new SearchParser());
		parsers.add(new PreferenceParser.PreferenceIgnoreParser());
		parsers.add(new LineRemover());
		LogicParserState parse = new BaseScopeParser<>(parsers).parse(new ParseCursor(content), new LogicParserState());
		Log.LOG.formatLine("INFO Parsing logic file took %.2f seconds", stopwatch.stop() / 1000);
		return parse.getLogicBase();
	}
}
