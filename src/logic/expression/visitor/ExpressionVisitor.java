package logic.expression.visitor;

import logic.expression.formula.*;
import logic.expression.term.Constant;
import logic.expression.term.Variable;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public abstract class ExpressionVisitor {

	//region Variables

	//endregion

	//region Construction

	//endregion

	//region Public methods
	public abstract void visit(And and);
	public abstract void visit(Constant constant);
	public abstract void visit(Clause clause);
	public abstract void visit(Equivalence equivalence);
	public abstract void visit(Implication implication);
	public abstract void visit(LogicalValue logicalValue);
	public abstract void visit(Not not);
	public abstract void visit(Or or);
	public abstract void visit(PredicateInstance instance);
	public abstract void visit(Variable variable);

	public void visit(InfixPredicateInstance instance) {
		visit((PredicateInstance) instance);
	}
	//endregion
}
