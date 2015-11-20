package clausal_discovery.test;

import clausal_discovery.configuration.Configuration;
import clausal_discovery.core.ClausalOptimization;
import clausal_discovery.core.Preferences;
import clausal_discovery.core.score.ScoringFunction;
import clausal_discovery.core.score.StatusClauseFunction;
import log.Log;
import logic.example.Example;
import pair.Pair;
import util.ParallelCalculator;
import vector.SafeList;
import vector.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * Created by samuelkolb on 30/04/15.
 *
 * @author Samuel Kolb
 */
public class OptimizationTester {

	//region Variables

	private final Configuration training;

	public Configuration getTraining() {
		return training;
	}

	private final Configuration testing;

	public Configuration getTesting() {
		return testing;
	}

	private final ClausalOptimization optimization;

	//endregion

	//region Construction

	/**
	 * Creates an optimization tester. In this step clausal optimization is run on the training set.
	 * @param training	The training configuration
	 * @param test		The test configuration
	 */
	public OptimizationTester(Configuration training, Configuration test) {
		this.training = training;
		this.testing = test;
		this.optimization = new ClausalOptimization(training);
		this.optimization.run();
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
		final Preferences preferences = generatePreferences(training.getLogicBase().getExamples(), testFunction)
				.resize(size).induceNoise(noise);
		double[] cFactors = {0.2};//Numbers.range(0.1, 1, 0.1);

		ScoreComparator trainComparator = new ScoreComparator(training.getLogicBase().getExamples());
		ScoreComparator testComparator = new ScoreComparator(testing.getLogicBase().getExamples());
		ParallelCalculator<Pair<Double, StatusClauseFunction>> calculator = new ParallelCalculator<>();
		for(double cFactor : cFactors)
			calculator.add(() -> {
				StatusClauseFunction function = optimization.getClauseFunction(preferences, cFactor,
						f -> trainComparator.score(testFunction, f));
				StatusClauseFunction testedFunction =
						function.copy(testing.getLogicBase(), testing.getBackgroundTheories());
				double score = testComparator.score(testFunction, testedFunction);
				return Pair.of(score, testedFunction);
			});

		List<Pair<Double, StatusClauseFunction>> scores = calculator.retrieveAll();
		int max = 0;
		Log.LOG.formatLine("%.2f: %f", cFactors[0], scores.get(0).getFirst());
		for(int i = 1; i < scores.size(); i++) {
			Log.LOG.formatLine("%.2f: %f", cFactors[i], scores.get(i).getFirst());
			if(scores.get(i).getFirst() > scores.get(max).getFirst())
				max = i;
		}
		Log.LOG.newLine();
		StatusClauseFunction function = scores.get(max).getSecond();
		for(int i = 0; i < function.getClauses().size(); i++)
			Log.LOG.formatLine("%f: %s", function.getWeights().get(i), function.getClauses().get(i));
		return scores.get(max).getFirst();
	}

	/**
	 * Creates preferences using the given examples and scoring function
	 * @param examples    The examples to create preferences over
	 * @param function    The function to induce the ordering
	 * @return	A preferences object
	 */
	public Preferences generatePreferences(SafeList<Example> examples, ScoringFunction function) {
		List<List<List<Example>>> orders = new ArrayList<>();
		for(int i = 0; i < examples.size(); i++)
			for(int j = i + 1; j < examples.size(); j++) {
				int compare = function.compare(examples.get(i), examples.get(j));
				if(compare > 0)
					orders.add(Arrays.asList(singletonList(examples.get(i)), singletonList(examples.get(j))));
				else if(compare < 0)
					orders.add(Arrays.asList(singletonList(examples.get(j)), singletonList(examples.get(i))));
				/*else
					orders.add(singletonList(Arrays.asList(examples.get(i), examples.get(j))));*/
			}
		Log.LOG.formatLine("Create %d preferences over %d examples", orders.size(), examples.size());
		return Preferences.newFromOrders(orders);
	}

	//endregion
}
