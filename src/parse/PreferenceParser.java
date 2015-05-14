package parse;

import clausal_discovery.core.Preferences;
import logic.example.Example;
import vector.Vector;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Parses preferences from a file
 *
 * @author Samuel Kolb
 */
public class PreferenceParser extends MatchParser<List<List<List<Example>>>> implements LocalParser<Preferences> {

	static class PreferenceIgnoreParser extends MatchParser<LogicParserState> {
		@Override
		public boolean matches(String string, LogicParserState parseState) throws ParsingError {
			return string.matches("pref.*\\n");
		}
	}

	static class EverythingParser extends MatchParser<List<List<List<Example>>>> {
		@Override
		public boolean matches(String string, List<List<List<Example>>> parseState) throws ParsingError {
			return string.matches(".*\\n") && !string.startsWith("pref");
		}
	}

	private final Map<String, Example> examples;

	/**
	 * Creates a new preference parser
	 * @param examples	The examples to resolve indices
	 */
	public PreferenceParser(Vector<Example> examples) {
		this.examples = new HashMap<>();
		for(Example example : examples)
			this.examples.put(example.getName(), example);
	}

	@Override
	public Preferences parse(String content) {
		List<ScopeParser<List<List<List<Example>>>>> parsers = Arrays.asList(this, new EverythingParser());
		ParseCursor parseCursor = new ParseCursor(content);
		return Preferences.newFromOrders(new BaseScopeParser<>(parsers).parse(parseCursor, new ArrayList<>()));
	}

	@Override
	public boolean matches(String string, List<List<List<Example>>> parseState) throws ParsingError {
		if(!string.matches("pref\\s+.*\\n"))
			return false;
		String[] parts = string.substring(4, string.length()).split(">");
		List<List<Example>> preference = new ArrayList<>();
		for(String part : parts)
			preference.add(Arrays.asList(part.trim().split("=")).stream()
					.map(s -> {
						if(!this.examples.containsKey(s))
							throw new NoSuchElementException("No example with the name: " + s);
						return this.examples.get(s.trim());
					})
					.collect(Collectors.toList()));
		parseState.add(preference);
		return true;
	}
}
