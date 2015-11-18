package clausal_discovery.core;

import basic.MathUtil;
import logic.expression.formula.Predicate;
import vector.SafeList;
import vector.Vector;

/**
 * Created by samuelkolb on 18/11/15.
 *
 * @author Samuel Kolb
 */
public interface Literal {

	class BasicLiteral implements Literal {

		private final Predicate predicate;

		private final Vector<Integer> variables;

		private final int rank;

		private final boolean isPositive;

		/**
		 * Creates a new literal instance.
		 * @param predicate		A predicate
		 * @param variables		The variables (represented as integers)
		 * @param isPositive	Whether this is a positive literal
		 */
		public BasicLiteral(Predicate predicate, Vector<Integer> variables, boolean isPositive) {
			this.predicate = predicate;
			this.variables = variables;
			this.rank = new SafeList<>(variables).foldLeft(-1, Math::max);
			this.isPositive = isPositive;
		}

		@Override
		public Predicate getPredicate() {
			return predicate;
		}

		@Override
		public Vector<Integer> getVariables() {
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
	}

	Predicate getPredicate();

	Vector<Integer> getVariables();

	int getRank();

	boolean isPositive();
}
