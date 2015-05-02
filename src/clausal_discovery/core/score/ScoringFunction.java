package clausal_discovery.core.score;

import clausal_discovery.validity.ValidityTable;
import logic.example.Example;
import vector.Vector;

import java.util.Comparator;

/**
 * The scoring function calculates scores for examples. It represents an optimization criterion.
 *
 * @author Samuel Kolb
 */
public interface ScoringFunction extends Comparator<Example> {

	/**
	 * Calculates the score for the given example
	 * @param example	The example to score
	 * @return	The calculated score
	 */
	double score(Example example);

	@Override
	default int compare(Example example1, Example example2) {
		return Double.compare(score(example1), score(example2));
	}
}
