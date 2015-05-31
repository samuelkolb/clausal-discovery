package clausal_discovery.core.score;

import clausal_discovery.validity.ValidityTable;
import log.Log;
import logic.example.Example;
import vector.Vector;

import java.util.Comparator;

/**
 * The scoring function calculates scores for examples. It represents an optimization criterion.
 *
 * @author Samuel Kolb
 */
public interface ScoringFunction extends Comparator<Example> {

	double TOLERANCE = 5.96e-8;

	/**
	 * Calculates the score for the given example
	 * @param example	The example to score
	 * @return	The calculated score
	 */
	double score(Example example);

	@Override
	default int compare(Example example1, Example example2) {
		double score1 = score(example1);
		double score2 = score(example2);
		// Look at features, look at tie / non-tie
		// |score1/score2 - 1| <  5.96e-8
		return Math.abs(score1/score2 - 1) < TOLERANCE ? 0 : Double.compare(score1, score2);
	}
}
