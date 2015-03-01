package util.parse;

import java.util.List;

/**
 * Created by samuelkolb on 24/02/15.
 *
 * @author Samuel Kolb
 */
public class BaseScopeParser<T> extends ScopeParser<T> {

	//region Variables
	private List<ScopeParser<T>> parsers;
	//endregion

	//region Construction

	/**
	 * Constructs a base scope parse which will use the given parsers
	 * @param parsers	The ground set of parsers
	 */
	public BaseScopeParser(List<ScopeParser<T>> parsers) {
		this.parsers = parsers;
	}

	//endregion

	//region Public methods

	@Override
	public List<ScopeParser<T>> getParsers() {
		return parsers;
	}

	@Override
	public boolean activatesWith(String string, T parseState) {
		return false;
	}

	@Override
	public boolean endsWith(String string, T parseState) {
		return false;
	}

	//endregion
}
