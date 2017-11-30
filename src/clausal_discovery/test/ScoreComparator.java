package clausal_discovery.test;

import clausal_discovery.core.score.ScoringFunction;
import logic.example.Example;
import pair.Pair;
import vector.SafeList;

import java.util.ArrayList;
import java.util.List;

/**
 * The score comparator calculates scores to compare scoring functions
 *
 * @author Samuel Kolb
 */
public class ScoreComparator {

	//region Variables

	// IVAR pairs - The pairs of examples used to calculate the score of an ordering

	private final List<Pair<Example, Example>> pairs;

	private List<Pair<Example, Example>> getPairs() {
		return pairs;
	}

	//endregion

	//region Construction

	/**
	 * Creates a score comparator
	 * @param examples	The examples to use
	 */
	public ScoreComparator(SafeList<Example> examples) {
		this.pairs = new ArrayList<>();
		for(int i = 0; i < examples.size(); i++)
			for(int j = i + 1; j < examples.size(); j++)
				pairs.add(new Pair.Implementation<>(examples.get(i), examples.get(j)));
	}

	//endregion

	//region Public methods

	/**
	 * Calculates the score of the actual scoring function ordering compared to the expected ordering
	 * @param expected	The expected scoring function
	 * @param actual	The actual scoring function
	 * @return	A score between 0 and 1 indicating the similarity between the two orderings
	 * 			A score of 0 indicates perfectly similar orderings, a score of 1 means no pair was ordered the same
	 */
	public double score(ScoringFunction expected, ScoringFunction actual) {
		int pairCount = 0, correctCount = 0;
		for(Pair<Example, Example> pair : getPairs()) {
			int expectedOrder = expected.compare(pair.getFirst(), pair.getSecond());
			if(expectedOrder != 0) {
				pairCount++;
				if(expectedOrder == actual.compare(pair.getFirst(), pair.getSecond()))
					correctCount ++;
			}
		}
		return correctCount / (double) pairCount;
	}

	//endregion
}
