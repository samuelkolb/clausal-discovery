package logic.expression.formula;

import logic.expression.visitor.ExpressionVisitor;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public class Not extends CompositeFormula {

	//region Variables

	//endregion

	//region Construction

	public Not(Formula formula) {
		super(formula);
	}

	//endregion

	//region Public methods

	public Formula getElement() {
		return getElement(0);
	}

	@Override
	public boolean isTrue() {
		return !getElement().isTrue();
	}

	@Override
	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);
	}

	@Override
	public CompositeFormula instance(Formula... elements) {
		if(elements.length != 1)
			throw new IllegalArgumentException();
		return new Not(elements[0]);
	}

	//endregion
}
