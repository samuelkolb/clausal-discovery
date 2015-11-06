package logic.theory;

import vector.Vector;
import logic.expression.formula.Formula;

import java.util.Collection;

/**
 * An inline theory contains multiple logical formulas
 *
 * @author Samuel Kolb
 */
public class InlineTheory implements Theory {

	private final Vector<Formula> formulas;

	public Vector<Formula> getFormulas() {
		return formulas;
	}

	/**
	 * Creates an inline theory
	 * @param formulas	The formulas that make up the theory
	 */
	public InlineTheory(Formula... formulas) {
		this(new Vector<>(formulas));
	}

	/**
	 * Creates an inline theory
	 * @param formulas	The formulas that make up the theory
	 */
	public <T extends Formula> InlineTheory(Collection<T> formulas) {
		this.formulas = new Vector<>(new Formula[formulas.size()], formulas);
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
