package clausal_discovery.core;

import clausal_discovery.instance.Instance;
import clausal_discovery.instance.PositionedInstance;
import logic.expression.formula.Atom;
import logic.expression.formula.Clause;
import logic.expression.formula.InfixPredicate;
import logic.expression.term.Variable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A method class to convert status clauses into logical clauses
 *
 * @author Samuel Kolb
 */
public class StatusClauseConverter implements Function<StatusClause, Clause> {

	public static final InfixPredicate INEQUALITY = new InfixPredicate("~=");

	/**
	 * Returns the Formula represented by the given status clause (applying object identity)
	 * @param clause	The status clause
	 * @return	A logical Formula
	 */
	@Override
	public Clause apply(StatusClause clause) {
		List<Atom> bodyAtoms = new ArrayList<>();
		List<Atom> headAtoms = new ArrayList<>();
		Map<Integer, Variable> variableMap = new HashMap<>();
		for(PositionedInstance instance : clause.getInstances())
			(instance.isInBody() ? bodyAtoms : headAtoms).add(instance.getInstance().makeAtom(variableMap));
		applyOI(variableMap.values(), bodyAtoms);
		return Clause.clause(bodyAtoms, headAtoms);
	}

	/**
	 * Converts lists of body and head instances to a formula (applying object identity)
	 * @param body	The body instances
	 * @param head	The head instances
	 * @return	A logical formula
	 */
	public Clause apply(List<Instance> body, List<Instance> head) {
		List<Atom> bodyAtoms = new ArrayList<>();
		List<Atom> headAtoms = new ArrayList<>();
		Map<Integer, Variable> variableMap = new HashMap<>();
		bodyAtoms.addAll(body.stream().map(instance -> instance.makeAtom(variableMap)).collect(Collectors.toList()));
		headAtoms.addAll(head.stream().map(instance -> instance.makeAtom(variableMap)).collect(Collectors.toList()));
		applyOI(variableMap.values(), bodyAtoms);
		return Clause.clause(bodyAtoms, headAtoms);
	}

	private void applyOI(Collection<Variable> variables, List<Atom> bodyAtoms) {
		Variable[] array = variables.toArray(new Variable[variables.size()]);
		for(int i = 0; i < array.length; i++)
			for(int j = i + 1; j < array.length; j++)
				if(array[i].getType().isSuperTypeOf(array[j].getType())
						|| array[j].getType().isSuperTypeOf(array[i].getType()))
					bodyAtoms.add(INEQUALITY.getInstance(array[i], array[j]));
	}

}
