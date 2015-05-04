package clausal_discovery.test;

import clausal_discovery.configuration.Configuration;
import clausal_discovery.core.ClausalOptimization;
import clausal_discovery.core.score.ClauseFunction;
import clausal_discovery.core.Preferences;
import clausal_discovery.core.score.ScoringFunction;
import clausal_discovery.core.score.StatusClauseFunction;
import clausal_discovery.validity.ValidityTable;
import log.Log;
import logic.example.Example;
import vector.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuelkolb on 30/04/15.
 *
 * @author Samuel Kolb
 */
public class OptimizationTester {

	//region Variables

	private final Configuration training;

	private final Configuration testing;

	//endregion

	//region Construction

	/**
	 * Creates an optimization tester
	 * @param examples	The training and test configurations
	 */
	public OptimizationTester(Configuration examples) {
		this.training = examples;
		this.testing = examples;
	}

	/**
	 * Creates an optimization tester
	 * @param training	The training configuration
	 * @param test		The test configuration
	 */
	public OptimizationTester(Configuration training, Configuration test) {
		this.training = training;
		this.testing = test;
	}

	//endregion

	//region Public methods

	/**
	 * Tests the optimization
	 * @param testFunction	The scoring function representing the underlying model
	 * @param size			The size factor (between 0 and 1) determining what percentage of preferences to use as input
	 * @param noise			The noise factor (between 0 and 1) determining what percentage of noise to use
	 * @return	The score
	 */
	public double test(ScoringFunction testFunction, double size, double noise) {
		Preferences preferences = generatePreferences(training.getLogicBase().getExamples(), testFunction);
		preferences = preferences.resize(size).induceNoise(noise);
		StatusClauseFunction function = new ClausalOptimization(training).run(preferences);
		ClauseFunction testedFunction = function.copy(testing.getLogicBase(), testing.getBackgroundTheories());
		return new ScoreComparator(testing.getLogicBase().getExamples()).score(testFunction, testedFunction);
	}

	/**
	 * Creates preferences using the given examples and scoring function
	 * @param examples	The examples to create preferences over
	 * @param function	The function to induce the ordering
	 * @return	A preferences object
	 */
	public Preferences generatePreferences(Vector<Example> examples, ScoringFunction function) {
		List<Vector<Example>> orders = new ArrayList<>();
		for(int i = 0; i < examples.size(); i++)
			for(int j = i + 1; j < examples.size(); j++) {
				int compare = function.compare(examples.get(i), examples.get(j));
				if(compare > 0)
					orders.add(new Vector<>(examples.get(i), examples.get(j)));
				else if(compare < 0)
					orders.add(new Vector<>(examples.get(j), examples.get(i)));
			}
		return Preferences.newFromOrders(orders);
	}

	//endregion
}
