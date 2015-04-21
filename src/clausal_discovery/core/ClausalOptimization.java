package clausal_discovery.core;

import basic.ArrayUtil;
import basic.FileUtil;
import basic.StringUtil;
import clausal_discovery.configuration.Configuration;
import clausal_discovery.validity.ParallelValidityCalculator;
import clausal_discovery.validity.ValidityCalculator;
import com.sun.deploy.trace.LoggerTraceListener;
import idp.FileManager;
import idp.IdpExecutor;
import log.Log;
import logic.example.Example;
import logic.expression.formula.Formula;
import logic.theory.Theory;
import runtime.Terminal;
import time.Stopwatch;
import util.TemporaryFile;
import vector.Vector;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static basic.StringUtil.*;
import static basic.StringUtil.join;

/**
 * Created by samuelkolb on 21/04/15.
 *
 * @author Samuel Kolb
 */
public class ClausalOptimization {

	static final FileManager FILE_MANAGER = new FileManager("temp");

	//region Variables
	private final Configuration configuration;

	public Configuration getConfiguration() {
		return configuration;
	}

	//endregion

	//region Construction

	/**
	 * Creates a clausal optimization object
	 * @param configuration	The configuration containing search directives
	 */
	public ClausalOptimization(Configuration configuration) {
		this.configuration = configuration;
	}

	//endregion

	//region Public methods

	public void run() {
		Stopwatch stopwatch = new Stopwatch(true);
		Log.LOG.saveState().off();
		ClausalDiscovery clausalDiscovery = new ClausalDiscovery(getConfiguration());
		List<StatusClause> hardConstraints = clausalDiscovery.findHardConstraints();
		prettyPrint("Hard Constraints", hardConstraints).off();
		List<StatusClause> softConstraints = clausalDiscovery.findSoftConstraints(hardConstraints);
		prettyPrint("Soft Constraints", softConstraints).revert();
		List<boolean[]> exampleValidity = validateExamples(softConstraints);
		for(int i = 0; i < getConfiguration().getLogicBase().getExamples().size(); i++)
			Log.LOG.printLine("Example " + i + ": " + Arrays.toString(exampleValidity.get(i)));
		Log.LOG.newLine();
		List<int[]> preferences = Arrays.asList(new int[]{0, 1}, new int[]{1, 2});
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < preferences.size(); i++) {
			int[] order = preferences.get(i);
			for(int j = 0; j < order.length; j++) {
				builder.append(order.length - j).append(" qid:").append(i + 1);
				for(int c = 0; c < softConstraints.size(); c++)
					builder.append(" ").append(c + 1).append(":").append(exampleValidity.get(order[j])[c] ? 1 : 0);
				builder.append(" #\n");
			}
		}
		File inputFile = FILE_MANAGER.createRandomFile("txt");
		TemporaryFile temporaryFile = new TemporaryFile(inputFile, builder.toString());

		File outputFile = FILE_MANAGER.createRandomFile("txt");
		String relativePath = "/executable/mac/svm_rank/svm_rank_learn";
		String path = FileUtil.getLocalFile(getClass().getResource(relativePath)).getAbsolutePath();
		String command = path + " -c " + preferences.size()*0.15 + " " + inputFile.getAbsolutePath() + " " + outputFile.getAbsolutePath();
		Log.LOG.printLine("Ready to run: " + command);
		Terminal.get().execute(command, true);
		Log.LOG.printLine("Command finished");
		temporaryFile.delete();
		String[] output = FileUtil.readFile(outputFile).split("\n");
		String[] lastLine = substring(output[output.length - 1], 2, -2).split(" ");
		double[] score = new double[softConstraints.size()];
		Log.LOG.formatLine("Calculations done in %.2f seconds", stopwatch.stop() / 1000).newLine();
		for(String attribute : lastLine) {
			String[] parts = attribute.split(":");
			score[Integer.parseInt(parts[0]) - 1] = Double.parseDouble(parts[1]);
		}
		for(int i = 0; i < score.length; i++)
			Log.LOG.printLine(i + ": " + frontPadCut(String.format("%f", score[i]), ' ', 10, true) + " : " + softConstraints.get(i));

	}

	private List<boolean[]> validateExamples(List<StatusClause> statusClauses) {
		List<Formula> formulas = statusClauses.stream().map(new StatusClauseConverter()).collect(Collectors.toList());
		List<boolean[]> examples = new ArrayList<>();
		LogicBase logicBase = getConfiguration().getLogicBase();
		Vector<Theory> background = getConfiguration().getBackgroundTheories();
		ValidityCalculator[] calculators = new ValidityCalculator[logicBase.getExamples().size()];
		List<LogicBase> logicBases = logicBase.split();
		for(int i = 0; i < logicBase.getExamples().size(); i++) {
			calculators[i] = new ParallelValidityCalculator(logicBases.get(i), IdpExecutor.get(), background);
			for(Formula formula : formulas)
				calculators[i].submitFormula(formula);
		}
		for(int i = 0; i < logicBase.getExamples().size(); i++) {
			boolean[] booleans = new boolean[formulas.size()];
			for(int j = 0; j < formulas.size(); j++)
				booleans[j] = calculators[i].isValid(formulas.get(j));
			calculators[i].shutdown();
			examples.add(booleans);
		}
		return examples;
	}

	//endregion

	private static Log prettyPrint(String title, List<StatusClause> clauses) {
		Log.LOG.on();
		Log.LOG.printTitle(title);
		for(int i = 0; i < clauses.size(); i++)
			Log.LOG.printLine((i + 1) + ": " + clauses.get(i));
		Log.LOG.newLine();
		return Log.LOG;
	}

}
