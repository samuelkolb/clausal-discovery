package logic.expression.formula;

import logic.expression.visitor.ExpressionVisitor;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public class And extends CompositeFormula {

	//region Variables

	//endregion

	//region Construction

	public And(Formula... elements) {
		super(elements);
		if(elements.length < 2)
			throw new IllegalArgumentException("AND needs at least two elements");
	}

	//endregion

	//region Public methods

	@Override
	public boolean isTrue() {
		for(Formula formula : getElements())
			if(!formula.isTrue())
				return false;
		return true;
	}
	@Override
	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);
	}

	@Override
	public CompositeFormula instance(Formula... elements) {
		return new And(elements);
	}


	//endregion
}
