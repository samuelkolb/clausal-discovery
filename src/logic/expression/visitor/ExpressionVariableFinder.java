package logic.expression.visitor;

import logic.expression.Expression;
import logic.expression.formula.*;
import logic.expression.term.Constant;
import logic.expression.term.Variable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by samuelkolb on 28/10/14.
 *
 * @author Samuel Kolb
 */
public class ExpressionVariableFinder extends ExpressionVisitor {

	//region Variables
	private final Set<Variable> variables;
	//endregion

	//region Construction

	public ExpressionVariableFinder() {
		this.variables = new HashSet<>();
	}

	//endregion

	//region Public methods

	public static Set<Variable> findVariables(Expression expression) {
		ExpressionVariableFinder finder = new ExpressionVariableFinder();
		expression.accept(finder);
		return finder.variables;
	}

	@Override
	public void visit(And and) {
		visitComposite(and);
	}

	@Override
	public void visit(Constant constant) {

	}

	@Override
	public void visit(Clause clause) {
		clause.getBody().accept(this);
		clause.getHead().accept(this);
	}

	@Override
	public void visit(Equivalence equivalence) {
		visitComposite(equivalence);
	}

	@Override
	public void visit(Implication implication) {
		visitComposite(implication);
	}

	@Override
	public void visit(LogicalValue logicalValue) {

	}

	@Override
	public void visit(Not not) {
		not.getElement().accept(this);
	}

	@Override
	public void visit(Or or) {
		visitComposite(or);
	}

	@Override
	public void visit(PredicateInstance instance) {
		for(int i = 0; i < instance.getPredicate().getArity(); i++)
			instance.getTerm(i).accept(this);
	}

	@Override
	public void visit(Variable variable) {
		this.variables.add(variable);
	}

	private void visitComposite(CompositeFormula compositeFormula) {
		for(int i = 0; i < compositeFormula.getElementCount(); i++)
			compositeFormula.getElement(i).accept(this);
	}
	//endregion
}
