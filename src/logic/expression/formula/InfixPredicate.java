package logic.expression.formula;

import logic.bias.Type;
import logic.expression.term.Term;

/**
 * Created by samuelkolb on 27/02/15.
 */
public class InfixPredicate extends Predicate {

	public InfixPredicate(String name) {
		super(name, 2);
	}

	public InfixPredicate(String name, Type type1, Type type2) {
		super(name, type1, type2);
	}

	@Override
	public InfixPredicateInstance getInstance(Term... terms) {
		if(terms.length != 2)
			throw new IllegalArgumentException("Infix predicates need exactly two arguments");
		return new InfixPredicateInstance(this, terms[0], terms[1]);
	}
}
