package logic.expression.visitor;

import logic.expression.Expression;
import logic.expression.formula.*;
import logic.expression.term.Constant;
import logic.expression.term.Variable;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public class ExpressionLogicPrinter extends ExpressionVisitor {

	//region Variables
	private final StringBuilder builder = new StringBuilder();

	protected String getString() {
		return builder.toString();
	}

	protected void addString(String string) {
		this.builder.append(string);
	}

	//endregion

	//region Construction

	protected ExpressionLogicPrinter() {

	}

	//endregion

	//region Public methods

	/**
	 * Prints a given expression using an expression logic printer
	 * @param expression	The expression to print
	 * @return	A textual representation of the given expression
	 */
	public static String print(Expression expression) {
		ExpressionLogicPrinter visitor = new ExpressionLogicPrinter();
		expression.accept(visitor);
		return visitor.getString();
	}

	protected void visit(CompositeFormula compositeFormula, String connector) {
		addString("(");
		for(int i = 0; i < compositeFormula.getElementCount(); i++) {
			if(i > 0)
				addString(connector);
			compositeFormula.getElement(i).accept(this);
		}
		addString(")");
	}

	@Override
	public void visit(And and) {
		visit(and, " & ");
	}

	@Override
	public void visit(Constant constant) {
		addString(constant.getName());
	}

	@Override
	public void visit(Clause clause) {
		addString("(");
		clause.getHead().accept(this);
		addString(" <- ");
		clause.getBody().accept(this);
		addString(")");
	}

	@Override
	public void visit(Equivalence equivalence) {
		addString("(");
		equivalence.getLeftSide().accept(this);
		addString(" <=> ");
		equivalence.getRightSide().accept(this);
		addString(")");
	}

	@Override
	public void visit(Implication implication) {
		addString("(");
		implication.getBody().accept(this);
		addString(" => ");
		implication.getHead().accept(this);
		addString(")");
	}

	@Override
	public void visit(LogicalValue logicalValue) {
		addString(logicalValue.isTrue() ? "true" : "false");
	}

	@Override
	public void visit(Not not) {
		addString("~");
		not.getElement().accept(this);
	}

	@Override
	public void visit(Or or) {
		visit(or, " | ");
	}

	@Override
	public void visit(PredicateInstance instance) {
		addString(instance.getPredicate().getName());
		if(instance.getPredicate().getArity() > 0) {
			addString("(");
			for(int i = 0; i < instance.getPredicate().getArity(); i++) {
				if(i > 0)
					addString(", ");
				instance.getTerm(i).accept(this);
			}
			addString(")");
		}
	}

	@Override
	public void visit(InfixPredicateInstance instance) {
		addString("(");
		instance.getTerm(0).accept(this);
		addString(" ");
		addString(instance.getPredicate().getName());
		addString(" ");
		instance.getTerm(1).accept(this);
		addString(")");
	}

	@Override
	public void visit(Variable variable) {
		addString(variable.getName());
	}

	//endregion
}
