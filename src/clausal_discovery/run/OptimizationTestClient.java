package clausal_discovery.run;

import basic.FileUtil;
import clausal_discovery.configuration.Configuration;
import clausal_discovery.core.ClausalOptimization;
import clausal_discovery.core.Constraints;
import clausal_discovery.core.score.ClauseFunction;
import clausal_discovery.core.score.ScoringFunction;
import clausal_discovery.core.score.StatusClauseFunction;
import clausal_discovery.test.OptimizationTester;
import clausal_discovery.validity.ValidityTable;
import log.Log;
import logic.expression.formula.Formula;
import logic.theory.Vocabulary;
import pair.TypePair;
import parse.ConstraintParser;
import parse.ParseException;
import util.Pair;
import vector.SafeList;
import vector.Vector;

import java.net.URL;
import java.util.List;

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

	/**
	 * Creates an optimization test client.
	 * @param logicName
	 * @param constraintsName
	 * @param variables
	 * @param terms
	 */
	public OptimizationTestClient(String logicName, String constraintsName, int variables, int terms) {
		configuration = Configuration.fromLocalFile(logicName, variables, terms);
		this.function = getScoringFunction(configuration, constraintsName);
	}

	/**
	 * Creates an optimization test client.
	 * @param configuration	The configuration
	 * @param constraints	The weighted constraints
	 */
	public OptimizationTestClient(Configuration configuration, Vector<Pair<Double, String>> constraints) {
		this.configuration = configuration;
		SafeList<Double> weights = new SafeList<>(constraints).map(Pair::getFirst);
		Vocabulary v = configuration.getLogicBase().getVocabulary();
		Vector<Formula> formulas = constraints.map(Formula.class, p -> ConstraintParser.parseClause(v, p.getSecond()));
		function = new ClauseFunction(weights, ValidityTable.create(configuration.getLogicBase(), formulas));
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
