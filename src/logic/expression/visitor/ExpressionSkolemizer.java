package logic.expression.visitor;

import vector.Vector;
import logic.expression.formula.*;
import logic.expression.term.Constant;
import logic.expression.term.Term;
import logic.expression.term.Variable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by samuelkolb on 24/10/14.
 *
 * @author Samuel Kolb
 */
public class ExpressionSkolemizer {

	//region Variables
	//endregion

	//region Construction

	public ExpressionSkolemizer() {
	}

	//endregion

	//region Public methods

	public Formula skolemize(Formula formula) {
		return skolemize(formula, new HashMap<Variable, Constant>());
	}

	private Formula skolemizePredicate(Map<Variable, Constant> substitutions, PredicateInstance instance) {
		Term[] terms = new Term[instance.getPredicate().getArity()];
		for(int i = 0; i < terms.length; i++)
			terms[i] = skolemizePart(instance.getTerm(i), substitutions);
		return instance.instance(terms);
	}

	//endregion

	private Formula skolemize(Formula formula, Map<Variable, Constant> substitutions) {
		if(formula instanceof CompositeFormula)
			return skolemize((CompositeFormula) formula, substitutions);
		if(formula instanceof Clause)
			return skolemize((Clause) formula, substitutions);
		if(formula instanceof Atom)
			return skolemize((Atom) formula, substitutions);
		throw new IllegalStateException();
	}

	private Term skolemizePart(Term term, Map<Variable, Constant> substitutions) {
		if(term instanceof Variable) {
			Variable variable = (Variable) term;
			if(!substitutions.containsKey(variable))
				substitutions.put(variable, new Constant());
			return substitutions.get(variable);
		}
		return term;
	}

	private Formula skolemize(Atom atom, Map<Variable, Constant> substitutions) {
		if(atom instanceof PredicateInstance)
			return skolemizePredicate(substitutions, (PredicateInstance) atom);
		return atom;
	}

	private Formula skolemize(Clause clause, Map<Variable, Constant> substitutions) {
		Atom[] head = skolemizeAtoms(clause.getHeadAtoms(), substitutions);
		Atom[] body = skolemizeAtoms(clause.getBodyAtoms(), substitutions);
		return Clause.clause(head, body);
	}

	private Formula skolemize(CompositeFormula formula, Map<Variable, Constant> variableMapping) {
		Formula[] result = new Formula[formula.getElementCount()];
		for(int i = 0; i < formula.getElementCount(); i++)
			result[i] = skolemize(formula.getElement(i), variableMapping);
		return formula.instance(result);
	}

	private Atom[] skolemizeAtoms(Vector<Atom> atoms, Map<Variable, Constant> variableMapping) {
		Atom[] result = new Atom[atoms.length];
		for(int i = 0; i < atoms.length; i++)
			result[i] = (Atom) skolemize(atoms.e(i), variableMapping);
		return result;
	}

}
