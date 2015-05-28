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

	public static final double STEP = 0.1;

	static {
		Log.LOG.addMessageFilter(message -> (message.MESSAGE == null || !message.MESSAGE.startsWith("INFO")));
		Log.LOG.addTransformer(new RelativeTimeTransformer());
	}

	/**
	 * Run the housing smart example
	 * @param args	Ignored command line arguments
	 */
	public static void main(String[] args) {
		String name = "housing_opt_test_small";
		OptimizationTestClient client = new OptimizationTestClient(name, name + "_1", 4, 3);
		scenario2(client);
	}

	private static void scenario1(OptimizationTestClient client) {
		Log.LOG.printLine("size  | noise | score");
		Log.LOG.printLine("- - - | - - - | - - -");
		Log.LOG.saveState().off();
		OptimizationTester tester = client.getTester();
		for(int size = 1; size <= 1; size++)
			for(int noise = 0; noise <= 0; noise++) {
				double fractionSize = size * STEP;
				double fractionNoise = noise * STEP;
				double score = client.score(tester, fractionSize, fractionNoise);
				Log.LOG.on().formatLine("%.3f | %.3f | %.3f", fractionSize, fractionNoise, score).off();
			}
		Log.LOG.revert();
	}

	private static void scenario2(OptimizationTestClient client) {
		Log.LOG.printLine("split | size  | noise | score");
		Log.LOG.printLine("- - - | - - - | - - - | - - -");
		Log.LOG.saveState().off();
		for(int split = 3; split <= 3; split++) {
			double fractionSplit = split * STEP;
			OptimizationTester tester = client.getTester(fractionSplit);
			for(int size = 3; size <= 3; size++) {
				for(int noise = 0; noise <= 0; noise++) {
					double fractionSize = size * STEP;
					double fractionNoise = noise * STEP;
					double score = client.score(tester, fractionSize, fractionNoise);
					Log.LOG.on().formatLine("%.3f | %.3f | %.3f | %.3f",
							fractionSplit, fractionSize, fractionNoise, score).off();
				}
			}
		}
		Log.LOG.revert();
	}
}
