package logic.expression.formula;

import logic.expression.visitor.ExpressionVisitor;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public class Implication extends CompositeFormula {

	//region Variables

	//endregion

	//region Construction

	public Implication(Formula body, Formula head) {
		super(body, head);
	}

	//endregion

	//region Public methods

	public Formula getBody() {
		return getElement(0);
	}

	public Formula getHead() {
		return getElement(1);
	}

	@Override
	public boolean isTrue() {
		return new Or(new Not(getBody()), getHead()).isTrue();
	}

	@Override
	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);
	}

	@Override
	public CompositeFormula instance(Formula... elements) {
		if(elements.length != 2)
			throw new IllegalArgumentException();
		return new Implication(elements[0], elements[1]);
	}

	//endregion
}
