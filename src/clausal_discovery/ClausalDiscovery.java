package clausal_discovery;

import basic.MathUtil;
import log.Log;
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

import java.util.Arrays;
import java.util.List;

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
		LogicBase base = new LogicParser().readLocalFile("coloring.logic");

		StopCriterion<StatusClause> stopCriterion = new EmptyQueueStopCriterion<>();
		VariableRefinement refinement = new VariableRefinement(base, 4, executor);
		List<StatusClause> initialNodes = Arrays.asList(new StatusClause());

		SearchAlgorithm<StatusClause> algorithm = new BreadthFirstSearch<>(refinement, stopCriterion, refinement);
		algorithm.addPlugin(new MaximalDepthPlugin<>(6));
		algorithm.addPlugin(new DuplicateEliminationPlugin<>(false));
		//algorithm.addPlugin(new ClausePrintingPlugin(refinement));
		algorithm.addPlugin(refinement);
		try {
			Result<StatusClause> result = Test.run(algorithm, initialNodes, 4);
			for(StatusClause statusClause : result.getSolutions())
				Log.LOG.printLine(ExpressionLogicPrinter.print(refinement.getClause(statusClause)));
			Log.LOG.newLine().printTitle(executor.entailmentCount + " entailment calculations took: " + MathUtil.round(executor.entailmentStopwatch.stop()/1000, 0) + ", " + executor.noEntailmentCount + " did not succeed.");
		} catch(Exception e) {
			Log.LOG.printTitle("Exception occurred");
			System.out.flush();
			System.err.flush();
			e.printStackTrace(System.err);
		} finally {
			executor.shutdown();
		}
	}
}
