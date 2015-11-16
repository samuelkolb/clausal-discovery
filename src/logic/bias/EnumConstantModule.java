package logic.bias;

import clausal_discovery.core.PredicateDefinition;
import clausal_discovery.instance.Instance;
import logic.expression.formula.And;
import logic.expression.formula.Atom;
import logic.expression.formula.Clause;
import logic.expression.formula.Formula;
import logic.expression.formula.Predicate;
import logic.expression.formula.PredicateInstance;
import logic.expression.term.Constant;
import logic.expression.term.Variable;
import vector.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by samuelkolb on 13/11/15.
 *
 * @author Samuel Kolb
 */
public class EnumConstantModule { // TODO contemplate implement as predicate definition?

	private class EnumPredicate extends PredicateDefinition {

		public EnumPredicate(Predicate predicate) {
			super(predicate);
		}

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
		this.predicateDefinition = new EnumPredicate(new Predicate(constant.getName(), constant.getType()));
	}

	public Predicate getPredicate() {
		return getPredicateDefinition().getPredicate();
	}

	public List<Formula> getBackgroundKnowledge() {
		Variable[] v = new Variable[]{new Variable("x"), new Variable("y")};
		Atom head = new PredicateInstance(new Predicate("=", 2), v[0], v[1]);
		Formula formula = Clause.horn(head, getPredicate().getInstance(v[0]), getPredicate().getInstance(v[1]));
		return Collections.singletonList(formula);
	}
}
