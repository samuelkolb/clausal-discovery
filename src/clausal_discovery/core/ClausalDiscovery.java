package clausal_discovery.core;

import clausal_discovery.run.Configuration;
import idp.IdpExecutor;
import version3.algorithm.*;
import version3.algorithm.implementation.BreadthFirstSearch;
import version3.plugin.DuplicateEliminationPlugin;
import version3.plugin.MaximalDepthPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The clausal discovery class sets up the search
 *
 * @author Samuel Kolb
 */
public class ClausalDiscovery {

	// IVAR executor - The executor used to delegate tasks to IDP

	private IdpExecutor executor = IdpExecutor.get();

	public IdpExecutor getExecutor() {
		return executor;
	}

	// IVAR excessTime - The time taken that entailment checks in the last run needed to round off the search

	private double excessTime;

	public double getExcessTime() {
		return excessTime;
	}

	/**
	 * Use the given configuration to find hard constraints in the data
	 * @param config	The configuration containing search instructions
	 * @return	A list of status clauses that represent hard constraints
	 */
	public List<StatusClause> findConstraints(Configuration config) {
		if(config.getBackgroundFile().isPresent())
			getExecutor().setBackgroundFile(config.getBackgroundFile().get());

		StopCriterion<StatusClause> stopCriterion = new EmptyQueueStopCriterion<>();
		VariableRefinement refinement = new VariableRefinement(config.getLogicBase(), config.getVariableCount(), getExecutor());
		List<StatusClause> initialNodes = Arrays.asList(new StatusClause());

		SearchAlgorithm<StatusClause> algorithm = new BreadthFirstSearch<>(refinement, stopCriterion, refinement);
		algorithm.addPlugin(new MaximalDepthPlugin<>(config.getClauseLength()));
		algorithm.addPlugin(new DuplicateEliminationPlugin<>(false));
		algorithm.addPlugin(refinement);

		List<StatusClause> statusClauses = makeList(algorithm.search(initialNodes));
		this.excessTime = refinement.getExcessTimer().stop();
		return statusClauses;
	}

	private List<StatusClause> makeList(Result<StatusClause> result) {
		List<StatusClause> list = new ArrayList<>();
		for(Node<StatusClause> clause : result)
			list.add(clause.getValue());
		return list;
	}
}
