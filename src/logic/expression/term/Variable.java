package logic.expression.term;

import logic.bias.Type;
import logic.expression.visitor.ExpressionVisitor;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public class Variable extends NamedTerm {

	//region Variables

	//endregion

	//region Construction

	public Variable(String name) {
		super(name);
	}

	public Variable(String name, Type type) {
		super(name, type);
	}

	//endregion

	//region Public methods
	@Override
	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);
	}

	@Override
	public boolean isGround() {
		return false;
	}



	//endregion
}
