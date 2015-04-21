package parse;

import log.Log;
import vector.Vector;
import vector.WriteOnceVector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuelkolb on 21/04/15.
 *
 * @author Samuel Kolb
 */
public class PreferenceParser extends MatchParser<List<Vector<Integer>>> implements LocalParser<List<Vector<Integer>>> {

	static class PreferenceIgnoreParser extends MatchParser<LogicParserState> {
		@Override
		public boolean matches(String string, LogicParserState parseState) throws ParsingError {
			return string.matches("pref.*\\n");
		}
	}

	static class EverythingParser extends MatchParser<List<Vector<Integer>>> {
		@Override
		public boolean matches(String string, List<Vector<Integer>> parseState) throws ParsingError {
			return string.matches(".*\\n") && !string.startsWith("pref");
		}
	}

	@Override
	public List<Vector<Integer>> parse(String content) {
		List<ScopeParser<List<Vector<Integer>>>> parsers = new ArrayList<>();
		parsers.add(this);
		parsers.add(new EverythingParser());
		return new BaseScopeParser<>(parsers).parse(new ParseCursor(content), new ArrayList<>());
	}

	@Override
	public boolean matches(String string, List<Vector<Integer>> parseState) throws ParsingError {
		if(!string.matches("pref.*\\n"))
			return false;
		String[] parts = string.substring(4, string.length()).split(">");
		Vector<Integer> preference = new WriteOnceVector<>(new Integer[parts.length]);
		for(String part : parts)
			preference.add(Integer.parseInt(part.trim()) - 1);
		parseState.add(preference);
		return true;
	}
}
