package logic.expression.formula;

import logic.expression.visitor.ExpressionVisitor;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public class Or extends CompositeFormula {

	//region Variables

	//endregion

	//region Construction

	public Or(Formula... elements) {
		super(elements);
		if(elements.length < 2)
			throw new IllegalArgumentException("AND needs at least two elements");
	}

	//endregion

	//region Public methods

	@Override
	public boolean isTrue() {
		for(Formula formula : getElements())
			if(formula.isTrue())
				return true;
		return false;
	}

	@Override
	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);
	}

	@Override
	public CompositeFormula instance(Formula... elements) {
		return new Or(elements);
	}

	//endregion
}
