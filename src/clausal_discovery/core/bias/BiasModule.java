package clausal_discovery.core.bias;

import clausal_discovery.core.Literal;
import clausal_discovery.core.LiteralSet;

/**
 * Created by samuelkolb on 18/11/15.
 *
 * @author Samuel Kolb
 */
public interface BiasModule {

	/**
	 * Returns the literal mask of this module.
	 * @return	A literal set, only included literals maybe used
	 */
	LiteralSet getMask();

	/**
	 * Extends the bias module with the given literal.
	 * @param literal	The new literal
	 * @return	A bias module that unlocks literals based on its previous state and the new literal
	 */
	BiasModule extend(Literal literal);
}
