package clausal_discovery.core;

import basic.ArrayUtil;
import basic.FileUtil;
import clausal_discovery.configuration.Configuration;
import clausal_discovery.core.score.ClauseFunction;
import clausal_discovery.core.score.StatusClauseFunction;
import clausal_discovery.test.ScoreComparator;
import clausal_discovery.validity.ValidatedClause;
import clausal_discovery.validity.ValidityTable;
import idp.FileManager;
import idp.IdpExpressionPrinter;
import log.Log;
import runtime.Terminal;
import time.Stopwatch;
import util.TemporaryFile;
import vector.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static basic.StringUtil.substring;

/**
 * Clausal optimization finds scoring functions using preferences
 *
 * @author Samuel Kolb
 */
public class ClausalOptimization {

	private class Run {

		private final Stopwatch stopwatch = new Stopwatch();

		private Vector<ValidatedClause> hardConstraints;

		private Vector<ValidatedClause> softConstraints;

		private ValidityTable validity;

		public Run run() {
			stopwatch.start();
			Configuration config = getConfiguration();
			ClausalDiscovery clausalDiscovery = new ClausalDiscovery(config);
			List<ValidatedClause> allClauses = clausalDiscovery.findSoftConstraints(new ArrayList<>());
			hardConstraints = new Vector<>(ValidatedClause.class, allClauses).filter(ValidatedClause::coversAll);
			softConstraints = new Vector<>(ValidatedClause.class, allClauses).filter(c -> !c.coversAll());
			prettyPrint("Hard Constraints", hardConstraints);
			prettyPrint("Soft Constraints", softConstraints);
			Vector<StatusClause> clauses = softConstraints.map(StatusClause.class, ValidatedClause::getClause);
			validity = ValidityTable.create(config, clauses);
			Log.LOG.formatLine("Calculations done in %.2f seconds", stopwatch.stop() / 1000).newLine();
			return this;
		}
	}

	static final FileManager FILE_MANAGER = new FileManager("temp");

	//region Variables
	private final Configuration configuration;

	public Configuration getConfiguration() {
		return configuration;
	}

	private Optional<Run> run = Optional.empty();

	private Run getRun() {
		if(!run.isPresent())
			run();
		return run.get();
	}

	public double getTime() {
		return getRun().stopwatch.stop();
	}

	public Vector<ValidatedClause> getHardConstraints() {
		return getRun().hardConstraints;
	}

	public Vector<ValidatedClause> getSoftConstraints() {
		return getRun().softConstraints;
	}

	public ValidityTable getSoftValidity() {
		return getRun().validity;
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
	 * Runs clausal optimization
	 * @return 	This clausal optimization object
	 */
	public ClausalOptimization run() {
		if(!this.run.isPresent())
			this.run = Optional.of(new Run().run());
		return this;
	}

	/* In reality:
	 * - Scores for clauses
	 * - Preferences
	 * - Hidden examples and preferences
	 *   - Rate example according to score and given preferences
	 * - Scores for theories
	 */

	/**
	 * Calculate the clause function fitting the given preferences
	 * @param preferences	The preferences
	 * @param cFactor		The cost factor, which influences the svm-rank algorithm
	 * @return	An array of scores for each clause
	 */
	public StatusClauseFunction getClauseFunction(Preferences preferences, double cFactor) {
		if(getSoftValidity().getClauseCount() == 0)
			return new StatusClauseFunction(new Vector<>(), new Vector<>(), getSoftValidity());
		return getFunction(preferences, cFactor, getSoftValidity(), getSoftConstraints());
	}

	/**
	* Calculate the clause function fitting the given preferences
	* @param preferences	The preferences
	* @param cFactor		The cost factor, which influences the svm-rank algorithm
	* @return	An array of scores for each clause
	*/
	public StatusClauseFunction getClauseFunction(Preferences preferences, double cFactor,
												  Function<ClauseFunction, Double> ratingFunction) {
		if(getSoftValidity().getClauseCount() == 0)
			return new StatusClauseFunction(new Vector<>(), new Vector<>(), getSoftValidity());
		ValidityTable validity = getSoftValidity();
		Vector<ValidatedClause> softClauses = getSoftConstraints();

		StatusClauseFunction function = getFunction(preferences, cFactor, validity, softClauses);
		/*
		double score = ratingFunction.apply(function);
		return improve(function, score, preferences, cFactor, validity, softClauses, ratingFunction);
		/*/return function;/**/
	}

	private StatusClauseFunction improve(StatusClauseFunction function, double score, Preferences preferences,
										 double cFactor, ValidityTable validity, Vector<ValidatedClause> softClauses,
										 Function<ClauseFunction, Double> ratingFunction) {
		if(softClauses.isEmpty())
			return function;
		int minimal = findMinimalWeight(function);
		ValidityTable newValidity = validity.removeClause(minimal);
		Vector<ValidatedClause> newSoftClauses = softClauses.leaveOut(minimal);
		StatusClauseFunction newFunction = getFunction(preferences, cFactor, newValidity, newSoftClauses);
		double newScore = ratingFunction.apply(newFunction);
		if(newScore < score)
			return function;
		return improve(newFunction, newScore, preferences, cFactor, newValidity, newSoftClauses, ratingFunction);
	}

	private int findMinimalWeight(StatusClauseFunction function) {
		int min = 0;
		for(int i = 0; i < function.getWeights().size(); i++)
			if(function.getWeights().get(i) < function.getWeights().get(min))
				min = i;
		return min;
	}

	private StatusClauseFunction getFunction(Preferences preferences, double cFactor, ValidityTable validity,
											 Vector<ValidatedClause> softClauses) {
		File inputFile = FILE_MANAGER.createRandomFile("txt");
		TemporaryFile temporaryFile = new TemporaryFile(inputFile, preferences.printOrderings(validity));
		File outputFile = FILE_MANAGER.createRandomFile("txt");
		String relativePath = "/executable/mac/svm_rank/svm_rank_learn";
		//String relativePath = "/executable/java/rank/RankLib.jar";
		String path = FileUtil.getLocalFile(getClass().getResource(relativePath)).getAbsolutePath();
		String command = String.format("%s -c %.8f -w 3 %s %s",
		//String command = String.format("java -jar %s -reg %s -train %s -ranker 4 -save %s",
				path, preferences.getCValue(cFactor), inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
		String debugOutput = Terminal.get().runCommand(command);
		temporaryFile.delete();
		if(!outputFile.exists()) {
			Log.LOG.printLine(debugOutput);
			throw new IllegalStateException("Output file \"" + outputFile.getAbsolutePath() + "\" not found");
		}
		String fileOutput = FileUtil.readFile(outputFile);
		outputFile.delete();
		String[] output = fileOutput.split("\n");
		String[] lastLine = substring(output[output.length - 1], 2, -2).split(" ");
		Double[] scores = new Double[validity.getClauseCount()];
		ArrayUtil.fill(scores, 0.0);
		for(String attribute : lastLine) {
			String[] parts = attribute.split(":");
			try {
				scores[Integer.parseInt(parts[0]) - 1] = Double.parseDouble(parts[1]);
			} catch(NumberFormatException e) {
				Log.LOG.on().error().formatLine("Caught exception: %s", e.getMessage());
				Log.LOG.newLine().printLine("File output:");
				Log.LOG.formatLine(fileOutput);
				System.exit(1);
			}
		}
		Vector<StatusClause> clauses = softClauses.map(StatusClause.class, ValidatedClause::getClause);
		return new StatusClauseFunction(clauses, new Vector<>(scores), validity);
	}

	//endregion

	private static void prettyPrint(String title, Collection<ValidatedClause> clauses) {
		Log.LOG.printTitle(title);
		int i = 0;
		for(ValidatedClause clause : clauses)
			Log.LOG.printLine(++i + ": " + clause);
		Log.LOG.newLine();
	}

}
