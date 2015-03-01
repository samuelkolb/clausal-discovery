package logic.expression;

import logic.expression.visitor.ExpressionVisitor;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public interface Expression {

	public void accept(ExpressionVisitor expressionVisitor);

	public boolean isGround();

}
