package logic.expression.formula;

import logic.expression.visitor.ExpressionVisitor;

/**
 * Created by samuelkolb on 23/10/14.
 *
 * @author Samuel Kolb
 */
public class Equivalence extends CompositeFormula {

	//region Variables

	//endregion

	//region Construction
	public Equivalence(Formula leftSide, Formula rightSide) {
		super(leftSide, rightSide);
	}
	//endregion

	//region Public methods

	public Formula getLeftSide() {
		return getElement(0);
	}

	public Formula getRightSide() {
		return getElement(1);
	}

	@Override
	public boolean isTrue() {
		return getLeftSide().isTrue() == getRightSide().isTrue();
	}

	@Override
	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);
	}

	@Override
	public CompositeFormula instance(Formula... elements) {
		if(elements.length != 2)
			throw new IllegalArgumentException();
		return new Equivalence(elements[0], elements[1]);
	}

	//endregion
}
