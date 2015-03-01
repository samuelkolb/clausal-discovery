package util.parse;

import java.util.List;

/**
 * Created by samuelkolb on 24/02/15.
 *
 * @author Samuel Kolb
 */
public abstract class MatchParser<T> extends ScopeParser<T> {

	//region Variables

	//endregion

	//region Construction

	//endregion

	//region Public methods

	@Override
	public final List<ScopeParser<T>> getParsers() {
		return null;
	}

	@Override
	public final boolean activatesWith(String string, T parseState) throws ParsingError{
		return matches(string, parseState);
	}

	@Override
	public final boolean endsWith(String string, T parseState) {
		return true;
	}

	public abstract boolean matches(String string, T parseState) throws ParsingError;

	//endregion
}
