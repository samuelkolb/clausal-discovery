package logic.theory;

import vector.SafeList;
import logic.expression.formula.Formula;

import java.util.Collection;

/**
 * An inline theory contains multiple logical formulas
 *
 * @author Samuel Kolb
 */
public class InlineTheory implements Theory {

	private final SafeList<Formula> formulas;

	public SafeList<Formula> getFormulas() {
		return formulas;
	}

	/**
	 * Creates an inline theory
	 * @param formulas	The formulas that make up the theory
	 */
	public InlineTheory(Formula... formulas) {
		this(new SafeList<>(formulas));
	}

	/**
	 * Creates an inline theory
	 * @param formulas	The formulas that make up the theory
	 */
	public <T extends Formula> InlineTheory(Collection<T> formulas) {
		this.formulas = new SafeList<>(new Formula[formulas.size()], formulas);
	}

	/**
	 * Creates a new inline theory containing all formulas of this theory and the given formula
	 * @param formula	The formula to add
	 * @return	A new inline theory
	 */
	public Theory addFormula(Formula formula) {
		return new InlineTheory(formulas.grow(formula));
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visit(this);
	}
}
