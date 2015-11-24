package logic.bias;

import clausal_discovery.core.Literal;
import clausal_discovery.core.PredicateDefinition;
import clausal_discovery.core.bias.LiteralBias;
import logic.expression.formula.Atom;
import logic.expression.formula.Clause;
import logic.expression.formula.Formula;
import logic.expression.formula.InfixPredicate;
import logic.expression.formula.Predicate;
import logic.expression.formula.PredicateInstance;
import logic.expression.term.Constant;
import logic.expression.term.Variable;
import vector.SafeList;

import java.util.List;

/**
 * Represents an enumeration type with a finite amount of constants.
 *
 * @author Samuel Kolb
 */
public class EnumType extends Type {

	private class EnumPredicateDefinition extends PredicateDefinition implements LiteralBias {

		private final Constant constant;

		private final Type type;

		public EnumPredicateDefinition(Constant constant) {
			super(new Predicate(constant.getName(), constant.getType()));
			this.constant = constant;
			this.type = EnumType.this;
		}

		@Override
		public List<Formula> getBackground() {
			// TODO Impossible: Faster(x0, x1) => Ferrari(x0) | BMW(x0) ?
			SafeList<Formula> formulas = SafeList.from(super.getBackground());
			Variable v1 = new Variable("x1", constant.getType()), v2 = new Variable("x2", constant.getType());
			Atom head = new InfixPredicate("=").getInstance(v1, v2);
			Formula formula = Clause.horn(head, getPredicate().getInstance(v1), getPredicate().getInstance(v2));
			return formulas.grow(formula);
		}

		@Override
		public boolean enables(Literal current, Literal test) {
			return false;
		}

		@Override
		public boolean disables(Literal current, Literal test) {
			PredicateDefinition definition = test.getDefinition();
			if(definition instanceof EnumPredicateDefinition) {
				EnumPredicateDefinition enumDefinition = (EnumPredicateDefinition) definition;
				return enumDefinition.type.equals(this.type);
			}
			return false;
		}
	}

	private final SafeList<Constant> constants;

	public SafeList<Constant> getConstants() {
		return constants;
	}

	/**
	 * Creates a new enum type.
	 * @param name		The name of the type
	 * @param constants	The constants
	 */
	public EnumType(String name, List<String> constants) {
		super(name);
		this.constants = new SafeList<>(constants, s -> new Constant(s, getSubtype(s)));
	}

	/**
	 * Returns a predicate definition for the given constant.
	 * @param constant	A constant of this enum type
	 * @return	A predicate definition
	 */
	public PredicateDefinition getPredicateDefinition(Constant constant) {
		if(!this.isSuperTypeOf(constant.getType())) {
			throw new IllegalArgumentException();
		}
		return new EnumPredicateDefinition(constant);
	}
}
