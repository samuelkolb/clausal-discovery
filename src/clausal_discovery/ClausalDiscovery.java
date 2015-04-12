package clausal_discovery;

import basic.FileUtil;
import basic.MathUtil;
import idp.FileManager;
import log.Log;
import log.OutputContainer;
import logic.expression.formula.Clause;
import version3.algorithm.EmptyQueueStopCriterion;
import version3.algorithm.Result;
import version3.algorithm.SearchAlgorithm;
import version3.algorithm.StopCriterion;
import version3.algorithm.implementation.BreadthFirstSearch;
import version3.example.Test;

import idp.IdpExecutor;
import logic.expression.visitor.ExpressionLogicPrinter;
import logic.parse.LogicParser;

import version3.plugin.DuplicateEliminationPlugin;
import version3.plugin.MaximalDepthPlugin;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * The clausal discovery class sets up the search
 *
 * @author Samuel Kolb
 */
public class ClausalDiscovery {

	/**
	 * Entry point for the given example
	 * @param args	Currently ignored
	 */
	public static void main(String[] args) {
		Log.LOG.addMessageFilter(message -> !message.MESSAGE.startsWith("INFO"));
		IdpExecutor executor = IdpExecutor.get();
		String name;
		if(args.length > 0)
			name = args[0];
		else {
			String[] examples = getExamples();
			Log.LOG.printLine("Choose example:");
			for(int i = 0; i < examples.length; i++)
				Log.LOG.printLine("[" + i + "] " + examples[i]);
			name = examples[new Scanner(System.in).nextInt()];
			name = name.substring(0, name.length() - 6);
			Log.LOG.printLine(name);
		}
		LogicBase base = new LogicParser().readLocalFile(name + ".logic");
		URL url = ClausalDiscovery.class.getResource("/examples/" + name + ".background");
		if(url != null)
			executor.setBackgroundFile(FileUtil.getLocalFile(url).getAbsolutePath());

		StopCriterion<StatusClause> stopCriterion = new EmptyQueueStopCriterion<>();
		VariableRefinement refinement = new VariableRefinement(base, 4, executor);
		List<StatusClause> initialNodes = Arrays.asList(new StatusClause());

		SearchAlgorithm<StatusClause> algorithm = new BreadthFirstSearch<>(refinement, stopCriterion, refinement);
		//algorithm.addPlugin(new MaximalDepthPlugin<>(3));
		algorithm.addPlugin(new DuplicateEliminationPlugin<>(false));
		algorithm.addPlugin(new ClausePrintingPlugin(refinement, false));
		algorithm.addPlugin(refinement);
		try {
			//OutputContainer container = Log.LOG.buffer();
			Result<StatusClause> result = Test.run(algorithm, initialNodes, 4);
			for(StatusClause statusClause : result.getSolutions())
				Log.LOG.printLine(ExpressionLogicPrinter.print(refinement.getClause(statusClause)));
			Log.LOG.newLine().printTitle(executor.entailmentCount + " entailment calculations took: " + MathUtil.round(executor.entailmentStopwatch.stop()/1000, 0) + ", " + executor.noEntailmentCount + " did not succeed.");
			//container.printToFile(FileManager.instance.createTempFile("txt"));
		} catch(Exception e) {
			Log.LOG.printTitle("Exception occurred");
			System.out.flush();
			System.err.flush();
			e.printStackTrace(System.err);
		} finally {
			executor.shutdown();
		}
	}

	private static String[] getExamples() {
		File folder = FileUtil.getLocalFile(ClausalDiscovery.class.getResource("/examples/"));
		return folder.list((dir, name) -> name.matches(".*\\.logic"));
	}
}
