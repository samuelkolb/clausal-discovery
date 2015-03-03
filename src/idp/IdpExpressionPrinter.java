package idp;

import logic.bias.Type;
import logic.expression.Expression;
import logic.expression.formula.Clause;
import logic.expression.formula.Implication;
import logic.expression.term.Variable;
import logic.expression.visitor.ExpressionLogicPrinter;
import logic.expression.visitor.ExpressionVariableFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by samuelkolb on 09/11/14.
 */
public class IdpExpressionPrinter extends ExpressionLogicPrinter {

	protected IdpExpressionPrinter() {

	}

	public static String print(Expression expression) {
		IdpExpressionPrinter expressionPrinter = new IdpExpressionPrinter();
		expression.accept(expressionPrinter);
		Set<Variable> variables = ExpressionVariableFinder.findVariables(expression);
		return print(new ArrayList<>(variables)) + expressionPrinter.getString();
	}

	@Override
	public void visit(Clause clause) {
		super.visit(new Implication(clause.getBody(), clause.getHead()));
	}

	private static String print(List<Variable> variables) {
		if(variables.isEmpty())
			return "";
		StringBuilder builder = new StringBuilder("!" + print(variables.get(0)));
		for(int i = 1; i < variables.size(); i++)
			builder.append(" ").append(print(variables.get(i)));
		return builder.append(": ").toString();
	}

	private static String print(Variable variable) {
		return variable.getType().equals(Type.UNDEFINED)
				? variable.getName()
				: variable.getName() + "[" + variable.getType().getName() + "]";
	}
}
