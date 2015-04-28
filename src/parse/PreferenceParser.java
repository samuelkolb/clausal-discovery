package parse;

import clausal_discovery.core.Preferences;
import log.Log;
import logic.example.Example;
import vector.Vector;
import vector.WriteOnceVector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuelkolb on 21/04/15.
 *
 * @author Samuel Kolb
 */
public class PreferenceParser extends MatchParser<List<Vector<Example>>> implements LocalParser<Preferences> {

	static class PreferenceIgnoreParser extends MatchParser<LogicParserState> {
		@Override
		public boolean matches(String string, LogicParserState parseState) throws ParsingError {
			return string.matches("pref.*\\n");
		}
	}

	static class EverythingParser extends MatchParser<List<Vector<Example>>> {
		@Override
		public boolean matches(String string, List<Vector<Example>> parseState) throws ParsingError {
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
		List<ScopeParser<List<Vector<Example>>>> parsers = new ArrayList<>();
		parsers.add(this);
		parsers.add(new EverythingParser());
		BaseScopeParser<List<Vector<Example>>> baseScopeParser = new BaseScopeParser<>(parsers);
		return Preferences.newFromOrders(baseScopeParser.parse(new ParseCursor(content), new ArrayList<>()));
	}

	@Override
	public boolean matches(String string, List<Vector<Example>> parseState) throws ParsingError {
		if(!string.matches("pref.*\\n"))
			return false;
		String[] parts = string.substring(4, string.length()).split(">");
		Vector<Example> preference = new WriteOnceVector<>(new Example[parts.length]);
		for(String part : parts)
			preference.add(this.examples.get(Integer.parseInt(part.trim()) - 1));
		parseState.add(preference);
		return true;
	}
}
