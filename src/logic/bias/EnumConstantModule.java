package logic.bias;

import clausal_discovery.core.PredicateDefinition;
import logic.expression.formula.Atom;
import logic.expression.formula.Clause;
import logic.expression.formula.Formula;
import logic.expression.formula.InfixPredicate;
import logic.expression.formula.Predicate;
import logic.expression.formula.PredicateInstance;
import logic.expression.term.Constant;
import logic.expression.term.Variable;
import vector.SafeList;

import java.util.Collections;
import java.util.List;

/**
 * Created by samuelkolb on 13/11/15.
 *
 * @author Samuel Kolb
 */
public class EnumConstantModule { // TODO contemplate implement as predicate definition?

	private class EnumPredicateDefinition extends PredicateDefinition {

		public EnumPredicateDefinition(Predicate predicate) {
			super(predicate);
		}

		@Override
		public List<Formula> getBackground() {
			Variable v1 = new Variable("x1"), v2 = new Variable("x2");
			Atom head = new PredicateInstance(new InfixPredicate("=", Type.GENERIC, Type.GENERIC), v1, v2);
			Formula formula = Clause.horn(head, getPredicate().getInstance(v1), getPredicate().getInstance(v2));
			return SafeList.from(formula);
		}
	}

	public class EnumType {

	}

	private final Constant constant;

	public Constant getConstant() {
		return constant;
	}

	private final PredicateDefinition predicateDefinition;

	public PredicateDefinition getPredicateDefinition() {
		return predicateDefinition;
	}

	/**
	 * Creates a module for the given constant.
	 * @param constant	The constant
	 */
	public EnumConstantModule(Constant constant) {
		this.constant = constant;
		this.predicateDefinition = new EnumPredicateDefinition(new Predicate(constant.getName(), constant.getType()));
	}

	public Predicate getPredicate() {
		return getPredicateDefinition().getPredicate();
	}

	public List<Formula> getBackgroundKnowledge() {
		Variable[] v = new Variable[]{new Variable("x"), new Variable("y")};
		Atom head = new PredicateInstance(new InfixPredicate("=", Type.GENERIC, Type.GENERIC), v[0], v[1]);
		Formula formula = Clause.horn(head, getPredicate().getInstance(v[0]), getPredicate().getInstance(v[1]));
		return Collections.singletonList(formula);
	}
}
