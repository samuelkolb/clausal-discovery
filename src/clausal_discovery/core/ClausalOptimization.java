package clausal_discovery.core;

import basic.FileUtil;
import clausal_discovery.configuration.Configuration;
import clausal_discovery.validity.ParallelValidityCalculator;
import clausal_discovery.validity.ValidityCalculator;
import idp.FileManager;
import idp.IdpExecutor;
import log.Log;
import logic.example.Example;
import logic.expression.formula.Formula;
import logic.theory.Theory;
import runtime.Terminal;
import vector.Vector;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
		Log.LOG.saveState().off();
		ClausalDiscovery clausalDiscovery = new ClausalDiscovery(getConfiguration());
		List<StatusClause> hardConstraints = clausalDiscovery.findHardConstraints();
		prettyPrint("Hard Constraints", hardConstraints).off();
		List<StatusClause> softConstraints = clausalDiscovery.findSoftConstraints(hardConstraints);
		prettyPrint("All Constraints", softConstraints).revert();
		List<boolean[]> exampleValidity = validateExamples(softConstraints);
		for(int i = 0; i < getConfiguration().getLogicBase().getExamples().size(); i++)
			Log.LOG.printLine("Example " + i + ": " + Arrays.toString(exampleValidity.get(i)));
		Log.LOG.newLine();
		List<int[]> preferences = Arrays.asList(new int[]{0, 1}, new int[]{1, 2});
		for(int i = 0; i < preferences.size(); i++) {
			int[] order = preferences.get(i);
			for(int j = 0; j < order.length; j++) {
				StringBuilder builder = new StringBuilder();
				builder.append(order.length - j).append(" qid:").append(i + 1);
				for(int c = 0; c < softConstraints.size(); c++)
					builder.append(" ").append(c + 1).append(":").append(exampleValidity.get(order[j])[c] ? 1 : 0);
				builder.append(" #");
				System.out.println(builder.toString());
			}
		}
		File inputFile = FILE_MANAGER.createRandomFile(".txt");
		try {
			inputFile.createNewFile();
		} catch(IOException e) {
			e.printStackTrace();
		}

		File outputFile = FILE_MANAGER.createRandomFile(".txt");
		String relativePath = "/executable/mac/svm_rank/svm_rank_learn";
		String path = FileUtil.getLocalFile(getClass().getResource(relativePath)).getAbsolutePath();
		Terminal.get().runCommand(path + " -c " + exampleValidity.size() + " " + inputFile.getAbsolutePath() + " " + outputFile.getAbsolutePath());
		String output = FileUtil.readFile(outputFile);
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
