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

	public IdpExecutor getExecutor() {
		return IdpExecutor.get();
	}

	// IVAR excessTime - The time taken that entailment checks in the last run needed to round off the search

	private double excessTime;

	public double getExcessTime() {
		return excessTime;
	}

	// IVAR configuration - The search configuration

	private final Configuration configuration;

	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Creates a new clausal discovery algorithm object
	 * @param configuration	The configuration to guide the search
	 */
	public ClausalDiscovery(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Use the given configuration to find hard constraints in the data
	 * @return	A list of status clauses that represent hard constraints
	 */
	public List<StatusClause> findHardConstraints() {
		return run(getConfiguration());
	}

	/**
	 * Use the given configuration to find constraints per example
	 * @return	A list of status clauses that hold on at least one example
	 */
	public List<StatusClause> findAllConstraints() {
		List<StatusClause> clauses = new ArrayList<>();
		for(Configuration config : getConfiguration().split())
			clauses.addAll(run(config));
		return clauses;
	}

	private List<StatusClause> run(Configuration configuration) {
		if(configuration.getBackgroundFile().isPresent())
			getExecutor().setBackgroundFile(configuration.getBackgroundFile().get());

		StopCriterion<StatusClause> stopCriterion = new EmptyQueueStopCriterion<>();
		int variableCount = configuration.getVariableCount();
		LogicBase logicBase = configuration.getLogicBase();
		VariableRefinement refinement = new VariableRefinement(logicBase, variableCount, getExecutor());
		List<StatusClause> initialNodes = Arrays.asList(new StatusClause());

		SearchAlgorithm<StatusClause> algorithm = new BreadthFirstSearch<>(refinement, stopCriterion, refinement);
		algorithm.addPlugin(new MaximalDepthPlugin<>(configuration.getClauseLength()));
		algorithm.addPlugin(new DuplicateEliminationPlugin<>(false));
		algorithm.addPlugin(refinement);
		configuration.addPlugins(algorithm);

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
