package clausal_discovery.core;

import logic.expression.formula.Predicate;
import vector.SafeList;

/**
 * Created by samuelkolb on 18/11/15.
 *
 * @author Samuel Kolb
 */
public interface Literal {

	class BasicLiteral implements Literal {

		private final PredicateDefinition definition;

		private final SafeList<Integer> variables;

		private final int rank;

		private final boolean isPositive;

		/**
		 * Creates a new literal instance.
		 * @param definition	The predicate definition
		 * @param variables		The variables (represented as integers)
		 * @param isPositive	Whether this is a positive literal
		 */
		public BasicLiteral(PredicateDefinition definition, SafeList<Integer> variables, boolean isPositive) {
			this.definition = definition;
			this.variables = variables;
			this.rank = new SafeList<>(variables).foldLeft(-1, Math::max);
			this.isPositive = isPositive;
		}

		@Override
		public PredicateDefinition getDefinition() {
			return definition;
		}

		@Override
		public SafeList<Integer> getVariables() {
			return variables;
		}

		@Override
		public int getRank() {
			return rank;
		}

		@Override
		public boolean isPositive() {
			return isPositive;
		}

		@Override
		public String toString() {
			return (isPositive() ? "" : "~") + getPredicate().getName() + getVariables().map(v -> "v" + v);
		}
	}

	PredicateDefinition getDefinition();

	default Predicate getPredicate() {
		return getDefinition().getPredicate();
	}

	SafeList<Integer> getVariables();

	int getRank();

	boolean isPositive();
}
