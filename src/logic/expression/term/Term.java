package logic.expression.term;

import logic.bias.Type;
import logic.expression.Expression;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public abstract class Term implements Expression {

	//region Variables

	private Type type;

	public Type getType() {
		return type;
	}

	/**
	 * Tests whether the given type can be assigned to this term
	 * @param type	The type to assign
	 * @return	True iff the given type can be assigned
	 */
	public boolean canAssign(Type type) {
		return getType().isSuperTypeOf(type);
	}

	/**
	 * Sets the type of this term to the given type
	 * @param type	The type to assign
	 * @throws java.lang.IllegalArgumentException	Iff <code>!canAssign(type)</code>
	 */
	public void setType(Type type) throws IllegalArgumentException {
		if(canAssign(type))
			this.type = type;
		else
			throw new IllegalArgumentException("Cannot set type of " + this + "(" + getType() + ") to " + type);
	}
	//endregion

	//region Construction

	Term() {
		this(Type.GENERIC);
	}

	Term(Type type) {
		this.type = type;
	}

	//endregion

	//region Public methods

	//endregion
}
