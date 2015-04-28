package clausal_discovery.core;

import logic.example.Example;
import logic.expression.formula.Clause;
import vector.Vector;

/**
 * Created by samuelkolb on 25/04/15.
 *
 * @author Samuel Kolb
 */
public interface ScoringFunction {

	public static class ClauseScoringFunction implements ScoringFunction {

		private final Vector<Double> weights;

		/**
		 * Creates a new weighted scoring function
		 * @param weights	The weights
		 */
		public ClauseScoringFunction(Vector<Double> weights) {
			this.weights = weights;
		}

		@Override
		public double score(Vector<Boolean> validity) {
			if(validity.size() != weights.size())
				throw new IllegalArgumentException("Validity vector does not match weights vector");
			double result = 0;
			for(int i = 0; i < validity.size(); i++)
				result += validity.get(i) ? weights.get(i) : 0;
			return result;
		}
	}

	/**
	 * Calculates the score for the given validity vector
	 * @param validity	A vector of booleans for which clauses the given example is valid
	 * @return	The calculated score
	 */
	public double score(Vector<Boolean> validity);
}
