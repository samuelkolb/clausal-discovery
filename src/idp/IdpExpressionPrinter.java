package idp;

import logic.expression.Expression;
import logic.expression.formula.Clause;
import logic.expression.formula.Implication;
import logic.expression.visitor.ExpressionLogicPrinter;

/**
 * Created by samuelkolb on 09/11/14.
 */
public class IdpExpressionPrinter extends ExpressionLogicPrinter {

	protected IdpExpressionPrinter() {

	}

	public static String print(Expression expression) {
		IdpExpressionPrinter expressionPrinter = new IdpExpressionPrinter();
		expression.accept(expressionPrinter);
		return expressionPrinter.getString();
	}

	@Override
	public void visit(Clause clause) {
		super.visit(new Implication(clause.getBody(), clause.getHead()));
	}
}
