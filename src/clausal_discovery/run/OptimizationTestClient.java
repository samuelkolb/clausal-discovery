package clausal_discovery.run;

import basic.FileUtil;
import clausal_discovery.configuration.Configuration;
import clausal_discovery.core.ClausalOptimization;
import clausal_discovery.core.Constraints;
import clausal_discovery.core.score.ScoringFunction;
import clausal_discovery.core.score.StatusClauseFunction;
import clausal_discovery.test.OptimizationTester;
import log.Log;
import pair.TypePair;
import parse.ConstraintParser;
import parse.ParseException;

import java.net.URL;

/**
 * Created by samuelkolb on 12/05/15.
 *
 * @author Samuel Kolb
 */
public class OptimizationTestClient {

	//region Variables

	private final Configuration configuration;

	private final ScoringFunction function;

	//endregion

	//region Construction

	public OptimizationTestClient(String logicName, String constraintsName, int variables, int terms) {
		configuration = Configuration.fromLocalFile(logicName, variables, terms);
		this.function = getScoringFunction(configuration, constraintsName);
	}

	//endregion

	//region Public methods

	public double score(OptimizationTester tester, double fractionPreferences, double fractionNoise) {
		return getScore(tester, fractionPreferences, fractionNoise);
	}

	public OptimizationTester getTester() {
		return new OptimizationTester(configuration, configuration);
	}

	public OptimizationTester getTester(double splitRatio) {
		TypePair<Configuration> configurations = configuration.split(splitRatio);
		return new OptimizationTester(configurations.getFirst(), configurations.getSecond());
	}

	//endregion
	private double getScore(OptimizationTester tester, double fractionPreferences, double fractionNoise) {
		return tester.test(function, fractionPreferences, fractionNoise);
	}

	private ScoringFunction getScoringFunction(Configuration test, String constraintsName) {
		URL url = getClass().getResource(String.format("/examples/%s.constraints", constraintsName));
		String content = FileUtil.readFile(FileUtil.getLocalFile(url));
		Constraints constraints = new ConstraintParser(test.getLogicBase()).parse(content);
		return constraints.getClauseFunction();
	}
}
