package logic.expression.formula;

import logic.expression.term.Term;
import logic.expression.visitor.ExpressionVisitor;

/**
 * Created by samuelkolb on 27/02/15.
 */
public class InfixPredicateInstance extends PredicateInstance {

	public InfixPredicateInstance(Predicate predicate, Term term1, Term term2) {
		super(predicate, term1, term2);
		if(predicate.getArity() != 2)
			throw new IllegalArgumentException("Infix predicates must contain exactly two arguments");
	}

	@Override
	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);
	}
}
