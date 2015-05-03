package clausal_discovery.core;

import basic.ArrayUtil;
import basic.FileUtil;
import clausal_discovery.configuration.Configuration;
import clausal_discovery.core.score.StatusClauseFunction;
import clausal_discovery.validity.ValidatedClause;
import clausal_discovery.validity.ValidityTable;
import idp.FileManager;
import log.Log;
import runtime.Terminal;
import time.Stopwatch;
import util.TemporaryFile;
import vector.Vector;

import java.io.File;
import java.util.Collection;

import static basic.StringUtil.frontPadCut;
import static basic.StringUtil.substring;

/**
 * Clausal optimization finds scoring functions using preferences
 *
 * @author Samuel Kolb
 */
public class ClausalOptimization {

	private class Run {

		private final Preferences preferences;

		private final Stopwatch stopwatch = new Stopwatch();

		public Run(Preferences preferences) {
			this.preferences = preferences;
		}

		private Vector<ValidatedClause> hardConstraints;

		private Vector<ValidatedClause> softConstraints;

		private ValidityTable validity;

		private StatusClauseFunction scoringFunction;

		public Run run() {
			stopwatch.start();
			Log.LOG.printLine("Finding hard constraints").saveState().off();
			Configuration config = getConfiguration();
			ClausalDiscovery clausalDiscovery = new ClausalDiscovery(config);
			hardConstraints = new Vector<>(ValidatedClause.class, clausalDiscovery.findHardConstraints());
			prettyPrint("Hard Constraints", hardConstraints).off();
			softConstraints = new Vector<>(ValidatedClause.class, clausalDiscovery.findSoftConstraints(hardConstraints));
			prettyPrint("Soft Constraints", softConstraints).revert();
			Vector<StatusClause> clauses = softConstraints.map(StatusClause.class, ValidatedClause::getClause);
			validity = ValidityTable.create(config, clauses);
			Log.LOG.formatLine("Calculations done in %.2f seconds", stopwatch.stop() / 1000).newLine();
			Double[] scores = getScores(preferences, validity);
			for(int i = 0; i < scores.length; i++)
				Log.LOG.printLine(i + ": " + frontPadCut(String.format("%f", scores[i]), ' ', 10, true) + " : " + softConstraints.get(i));
			scoringFunction = new StatusClauseFunction(clauses, new Vector<>(scores), validity);
			return this;
		}
	}

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

	/**
	 * Run clausal optimization
	 * @param preferences    The preferences to use
	 * @return	A scoring function learned using the given preferences
	 */
	public StatusClauseFunction run(Preferences preferences) {
		return new Run(preferences).run().scoringFunction;
	}

	/* In reality:
	 * - Scores for clauses
	 * - Preferences
	 * - Hidden examples and preferences
	 *   - Rate example according to score and given preferences
	 * - Scores for theories
	 */

	/**
	 * Calculate the scores of the given preferences for the given validity table
	 * @param preferences	The preferences
	 * @param validity		The validity table
	 * @return	An array of scores for each clause
	 */
	public Double[] getScores(Preferences preferences, ValidityTable validity) {
		File inputFile = FILE_MANAGER.createRandomFile("txt");
		TemporaryFile temporaryFile = new TemporaryFile(inputFile, preferences.printOrderings(validity));
		File outputFile = FILE_MANAGER.createRandomFile("txt");
		String relativePath = "/executable/mac/svm_rank/svm_rank_learn";
		String path = FileUtil.getLocalFile(getClass().getResource(relativePath)).getAbsolutePath();
		String command = String.format("%s -c %f %s %s",
				path, preferences.getCValue(), inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
		Terminal.get().execute(command, true);
		//temporaryFile.delete();
		String fileOutput = FileUtil.readFile(outputFile);
		String[] output = fileOutput.split("\n");
		String[] lastLine = substring(output[output.length - 1], 2, -2).split(" ");
		Double[] scores = new Double[validity.getClauseCount()];
		ArrayUtil.fill(scores, 0.0);
		for(String attribute : lastLine) {
			String[] parts = attribute.split(":");
			scores[Integer.parseInt(parts[0]) - 1] = Double.parseDouble(parts[1]);
		}
		return scores;
	}

	//endregion

	private static Log prettyPrint(String title, Collection<ValidatedClause> clauses) {
		Log.LOG.on();
		Log.LOG.printTitle(title);
		int i = 0;
		for(ValidatedClause clause : clauses)
			Log.LOG.printLine(++i + ": " + clause);
		Log.LOG.newLine();
		return Log.LOG;
	}

}
