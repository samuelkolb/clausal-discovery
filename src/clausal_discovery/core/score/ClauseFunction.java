package clausal_discovery.core.score;

import cern.colt.bitvector.BitVector;
import clausal_discovery.core.LogicBase;
import clausal_discovery.core.StatusClause;
import clausal_discovery.validity.ValidityTable;
import log.Log;
import logic.example.Example;
import logic.theory.Theory;
import vector.SafeList;
import vector.Vector;

/**
 * Created by samuelkolb on 01/05/15.
 *
 * @author Samuel Kolb
 */
public class ClauseFunction implements ScoringFunction {

	private final SafeList<Double> weights;

	public Vector<Double> getWeights() {
		return new Vector<>(Double.class, weights);
	}

	private final ValidityTable validityTable;

	public ValidityTable getValidity() {
		return validityTable;
	}

	/**
	 * Creates a new weighted scoring function
	 * @param weights       The weights
	 * @param validityTable The validity table containing validity vectors for the clauses in this function
	 */
	public ClauseFunction(SafeList<Double> weights, ValidityTable validityTable) {
		if(weights.size() != validityTable.getClauseCount())
			throw new IllegalArgumentException(String.format("Number of weights and table do not match, %d vs %d",
					weights.size(), validityTable.getClauseCount()));
		this.weights = weights;
		this.validityTable = validityTable;
	}

	@Override
	public double score(Example example) {
		BitVector validity = this.validityTable.getValidity(example);
		double result = 0;
		for(int i = 0; i < validity.size(); i++)
			result += validity.get(i) ? weights.get(i) : 0;
		return result;
	}

	@Override
	public String toString() {
		return getWeights().toString();
	}
}
