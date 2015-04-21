package parse;

import clausal_discovery.core.LogicBase;

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
		List<ScopeParser<LogicParserState>> parsers = new ArrayList<>();
		parsers.add(new CommentParser());
		parsers.add(new TypeDefParser());
		parsers.add(new PredicateDefParser());
		parsers.add(new ExampleParser());
		parsers.add(new SearchParser());
		parsers.add(new PreferenceParser.PreferenceIgnoreParser());
		parsers.add(new LineRemover());
		return new BaseScopeParser<>(parsers).parse(new ParseCursor(content), new LogicParserState()).getLogicBase();
	}
}
