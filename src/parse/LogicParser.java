package parse;

import clausal_discovery.core.LogicBase;
import log.Log;
import time.Stopwatch;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The logic parser parses logic files
 *
 * @author Samuel Kolb
 */
public class LogicParser implements LocalParser<LogicBase> {

	@Override
	public LogicBase parse(String content) {
		Stopwatch stopwatch = new Stopwatch(true);

		String[] parts = content.split("generate");
		if(parts.length > 1) {
			parts[1] = parts[1].trim();
			Map<Integer, List<String>> options = new LinkedHashMap<>();
			Pattern pattern = Pattern.compile("\\s*#(\\d+) (.*)\n");
			Matcher matcher = pattern.matcher(parts[1]);
			while(matcher.find())
				options.put(Integer.parseInt(matcher.group(1)), Arrays.asList(matcher.group(2).trim().split("\\s+")));
			parts[1] = parts[1].replaceAll("\\s*#(\\d+) (.*)\n", "");
			List<String> list = produce(1, options, new ArrayList<>(), parts[1]);
			StringBuilder builder = new StringBuilder(parts[0]);
			for(int i = 0; i < list.size(); i++)
				builder.append("\n").append("example ").append(list.get(i).replace("$n", Integer.toString(i + 1)));
			content = builder.toString();
		}

		List<ScopeParser<LogicParserState>> parsers = new ArrayList<>();
		parsers.add(new CommentParser());
		parsers.add(new TypeDefParser());
		parsers.add(new PredicateDefParser());
		parsers.add(new ExampleParser());
		parsers.add(new SearchParser());
		parsers.add(new PreferenceParser.PreferenceIgnoreParser());
		parsers.add(new LineRemover());
		LogicParserState parse = new BaseScopeParser<>(parsers).parse(new ParseCursor(content), new LogicParserState());
		Log.LOG.formatLine("Parsing logic file took %.2f seconds", stopwatch.stop() / 1000);
		return parse.getLogicBase();
	}

	private List<String> produce(Integer key, Map<Integer, List<String>> options, List<String> list, String text) {
		if(options.containsKey(key))
			for(String string : options.get(key))
				produce(key + 1, options, list, text.replace("$" + key, string));
		else
			list.add(text + "\n");
		return list;
	}
}
