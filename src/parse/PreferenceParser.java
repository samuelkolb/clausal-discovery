package parse;

import clausal_discovery.core.Preferences;
import logic.example.Example;
import vector.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

	private final Vector<Example> examples;

	/**
	 * Creates a new preference parser
	 * @param examples	The examples to resolve indices
	 */
	public PreferenceParser(Vector<Example> examples) {
		this.examples = examples;
	}

	@Override
	public Preferences parse(String content) {
		List<ScopeParser<List<List<List<Example>>>>> parsers = Arrays.asList(this, new EverythingParser());
		ParseCursor parseCursor = new ParseCursor(content);
		return Preferences.newFromOrders(new BaseScopeParser<>(parsers).parse(parseCursor, new ArrayList<>()));
	}

	@Override
	public boolean matches(String string, List<List<List<Example>>> parseState) throws ParsingError {
		if(!string.matches("pref.*\\n"))
			return false;
		String[] parts = string.substring(4, string.length()).split(">");
		List<List<Example>> preference = new ArrayList<>();
		for(String part : parts)
			preference.add(new ArrayList<>(Arrays.asList(part.trim().split("="))).stream()
					.map(s -> Integer.parseInt(s.trim()) - 1)
					.map(this.examples::get)
					.collect(Collectors.toList()));
		parseState.add(preference);
		return true;
	}
}
