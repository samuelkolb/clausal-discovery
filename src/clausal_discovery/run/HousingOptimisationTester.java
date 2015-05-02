package clausal_discovery.run;

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
import logic.example.Example;
import logic.expression.formula.Formula;
import logic.theory.Vocabulary;
import parse.ParseException;
import vector.Vector;

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
		Log.LOG.addTransformer(new LinkTransformer());
	}

	/**
	 * Run the housing smart example
	 * @param args	Ignored command line arguments
	 */
	public static void main(String[] args) {
		try {
			Configuration configuration = Configuration.fromLocalFile("housing_opt_test_small", 4, 3);
			OptimizationTester tester = new OptimizationTester(configuration);
			ScoringFunction testFunction = getFunction(configuration);
			Log.LOG.printLine("Score: " + tester.test(testFunction, 1, 0));
		} catch(ParseException e) {
			Log.LOG.on().printLine("Error occurred while parsing " + "housing_opt_test_small").printLine(e.getMessage());
		}
	}

	private static ClauseFunction getFunction(Configuration configuration) {
		Vocabulary vocabulary = configuration.getLogicBase().getVocabulary();
		List<Formula> clauses = new ArrayList<>();
		clauses.add(parseClause(vocabulary, "live_in(0) => low_crime(0)"));
		clauses.add(parseClause(vocabulary, "work_in(0) & school_in(1) => false"));
		clauses.add(parseClause(vocabulary, "school_in(0) => low_crime(0)"));
		clauses.add(parseClause(vocabulary, "live_in(0) => cheap(0)"));
		return new ClauseFunction(new Vector<>(0.5, 0.25, 1.0, 0.5), ValidityTable.create(configuration.getLogicBase(), clauses));
	}

	private static Formula parseClause(Vocabulary vocabulary, String string) {
		String[] twoParts = string.split(" => ");
		String[] bodyParts = twoParts[0].split(" & ");
		String[] headParts = twoParts[1].split(" | ");
		List<Instance> bodyInstances = new ArrayList<>();
		for(String bodyPart : bodyParts)
			bodyInstances.add(parseInstance(vocabulary, bodyPart));
		List<Instance> headInstances = new ArrayList<>();
		for(String headPart : headParts)
			if(!headPart.equals("false"))
				headInstances.add(parseInstance(vocabulary, headPart));
		return new StatusClauseConverter().apply(bodyInstances, headInstances);
	}

	private static Instance parseInstance(Vocabulary voc, String part) {
		String[] split = part.substring(0, part.length() - 1).split("\\(");
		return voc.getInstance(split[0], new Vector<>(split[1].split(" ")).map(Integer.class, Integer::parseInt));
	}
}
