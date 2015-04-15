package clausal_discovery.core;

import clausal_discovery.instance.PositionedInstance;
import logic.expression.formula.Atom;
import logic.expression.formula.Clause;
import logic.expression.term.Variable;

import java.util.*;
import java.util.function.Function;

/**
 * A method class to convert status clauses into logical clauses
 *
 * @author Samuel Kolb
 */
public class StatusClauseConverter implements Function<StatusClause, Clause> {

	/**
	 * Returns the Formula represented by the given status clause
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

	private void applyOI(Collection<Variable> variables, List<Atom> bodyAtoms) {
		Variable[] array = variables.toArray(new Variable[variables.size()]);
		for(int i = 0; i < array.length; i++)
			for(int j = i + 1; j < array.length; j++)
				if(array[i].getType().isSuperTypeOf(array[j].getType())
						|| array[j].getType().isSuperTypeOf(array[i].getType()))
					bodyAtoms.add(VariableRefinement.INEQUALITY.getInstance(array[i], array[j]));
	}

}
