package logic.theory;

import vector.Vector;
import vector.WriteOnceVector;
import logic.expression.formula.Formula;

import java.util.Collection;

/**
 * Created by samuelkolb on 09/11/14.
 */
public class Theory {

	private final Vector<Formula> formulas;

	public Vector<Formula> getFormulas() {
		return formulas;
	}

	public Theory(Formula... formulas) {
		this(new Vector<>(formulas));
	}

	public <T extends Formula> Theory(Collection<T> formulas) {
		this.formulas = new WriteOnceVector<>(new Formula[formulas.size()]);
		for(Formula formula : formulas)
			this.formulas.add(formula);
	}

	public Theory addFormula(Formula formula) {
		return new Theory(formulas.grow(formula));
	}
}
