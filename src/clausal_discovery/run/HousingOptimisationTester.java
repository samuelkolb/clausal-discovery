package clausal_discovery.run;

import basic.FileUtil;
import clausal_discovery.configuration.Configuration;
import clausal_discovery.core.*;
import clausal_discovery.core.score.ClauseFunction;
import clausal_discovery.core.score.ScoringFunction;
import clausal_discovery.instance.Instance;
import clausal_discovery.test.OptimizationTester;
import clausal_discovery.validity.ValidityTable;
import idp.IdpExpressionPrinter;
import log.LinkTransformer;
import log.Log;
import log.RelativeTimeTransformer;
import log.TimeTransformer;
import logic.example.Example;
import logic.expression.formula.Formula;
import logic.theory.Vocabulary;
import pair.TypePair;
import parse.ConstraintParser;
import parse.ParseException;
import vector.Vector;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by samuelkolb on 01/05/15.
 *
 * @author Samuel Kolb
 */
public class HousingOptimisationTester {

	static {
		Log.LOG.addMessageFilter(message -> (message.MESSAGE == null || !message.MESSAGE.startsWith("INFO")));
		Log.LOG.addTransformer(new RelativeTimeTransformer());
	}

	/**
	 * Run the housing smart example
	 * @param args	Ignored command line arguments
	 */
	public static void main(String[] args) {
		try {
			Configuration configuration = Configuration.fromLocalFile("housing_opt_test_small", 4, 3);
			scenario1(configuration);
		} catch(ParseException e) {
			Log.LOG.on().printLine("Error occurred while parsing " + "housing_opt_test_small").printLine(e.getMessage());
		}
	}

	private static void scenario1(Configuration configuration) {
		OptimizationTester tester = new OptimizationTester(configuration);
		ScoringFunction testFunction = getScoringFunction(configuration);
		Log.LOG.newLine().printLine("Score: " + tester.test(testFunction, 1, 0));
	}

	private static void scenario2(Configuration configuration) {
		TypePair<Configuration> configurations = configuration.split(0.5);
		Configuration train = configurations.one();
		Configuration test = configurations.two();
		OptimizationTester tester = new OptimizationTester(train, test);
		ScoringFunction testFunction = getScoringFunction(configuration);
		Log.LOG.printLine("Score: " + tester.test(testFunction, 1, 0));
	}

	private static ScoringFunction getScoringFunction(Configuration test) {
		URL url = HousingOptimisationTester.class.getResource("/examples/housing_opt_test_small_1.constraints");
		String content = FileUtil.readFile(FileUtil.getLocalFile(url));
		Constraints constraints = new ConstraintParser(test.getLogicBase()).parse(content);
		return constraints.getClauseFunction();
	}
}
