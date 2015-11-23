package clausal_discovery.core.bias;

import basic.ArrayUtil;
import clausal_discovery.core.PredicateDefinition;
import clausal_discovery.instance.Instance;
import idp.IdpExpressionPrinter;
import log.Log;
import logic.bias.Type;
import logic.expression.formula.Atom;
import logic.expression.formula.Clause;
import logic.expression.formula.Formula;
import logic.expression.formula.InfixPredicate;
import logic.expression.formula.Predicate;
import logic.expression.formula.PredicateInstance;
import logic.expression.term.Term;
import logic.expression.term.Variable;
import logic.theory.InlineTheory;
import logic.theory.Theory;
import math.Range;
import util.Numbers;
import vector.SafeList;
import vector.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by samuelkolb on 20/11/15.
 *
 * @author Samuel Kolb
 */
public class SymmetricPredicateDefinition extends PredicateDefinition {

	/**
	 * Creates a new symmetric predicate definition.
	 * @param predicate	The predicate.
	 */
	public SymmetricPredicateDefinition(Predicate predicate) {
		super(predicate);
	}

	@Override
	public boolean accepts(SafeList<Integer> variables) {
		if(!super.accepts(variables)) {
			return false;
		}
		// Check if the variables are sorted
		return sorted(variables);
	}

	@Override
	public SafeList<Integer> transform(SafeList<Integer> variables) {
		variables = super.transform(variables);
		return sorted(variables) ? variables : variables.sortedCopy();
	}

	protected Boolean sorted(SafeList<Integer> variables) {
		return variables.foldPast(true, -1, (initial, prev, var) -> initial && var >= prev);
	}

	@Override
	public List<Formula> getBackground() {
		List<Formula> list = new ArrayList<>();
		SafeList<Integer> variableIndices = SafeList.from(Range.integerRange(1, getPredicate().getArity()).getAll());
		SafeList<Term> terms = variableIndices.map(v -> new Variable("v" + v, getTypes().first()));
		PredicateInstance body = getPredicate().getInstance(terms);
		List<Numbers.Permutation> permutations = Numbers.getPermutations(getPredicate().getArity());
		for(int i = 1; i < permutations.size(); i++) {
			SafeList<Term> headTerms = SafeList.from(permutations.get(i).applyList(terms));
			PredicateInstance head = new PredicateInstance(getPredicate(), headTerms);
			list.add(Clause.horn(head, body));
			Log.LOG.printLine(IdpExpressionPrinter.print(list.get(list.size() - 1)));
		}
		return list;
		/*
		Variable v1 = new Variable("x1"), v2 = new Variable("x2");
		Atom head = new PredicateInstance(new InfixPredicate("=", Type.GENERIC, Type.GENERIC), v1, v2);
		Formula formula = Clause.horn(head, getPredicate().getInstance(v1), getPredicate().getInstance(v2));
		return SafeList.from(formula);*/
	}

	@Override
	public List<PredicateInstance> getGroundInstances(Term[] terms) {
		SafeList<Numbers.Permutation> permutations = SafeList.from(Numbers.getPermutations(terms.length));
		return permutations.map(p -> getPredicate().getInstance(p.applyArray(terms)));
	}

	@Override
	protected void checkIfValid(Predicate predicate) {
		super.checkIfValid(predicate);
		if(predicate.getArity() > 0) {
			Type type = predicate.getTypes().get(0);
			for(int i = 1; i < predicate.getArity(); i++)
				if(!type.equals(predicate.getTypes().get(i)))
					throw new IllegalArgumentException("In symmetric predicates all arguments must have the same type");
		}

	}
}
