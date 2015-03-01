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

	public void setType(Type type) {
		if(this.type == Type.UNDEFINED)
			this.type = type;
		else if(type != Type.UNDEFINED && this.type != type)
			throw new IllegalStateException("Type " + type + " could not be assigned to variable of type " +this.type);
	}
	//endregion

	//region Construction

	Term() {
		this(Type.UNDEFINED);
	}

	Term(Type type) {
		this.type = type;
	}

	//endregion

	//region Public methods

	//endregion
}
