package clausal_discovery.core;

import clausal_discovery.configuration.Configuration;
import clausal_discovery.validity.ValidatedClause;
import idp.IdpExecutor;
import logic.expression.formula.Formula;
import logic.theory.InlineTheory;
import logic.theory.Theory;
import vector.Vector;
import version3.algorithm.*;
import version3.algorithm.implementation.BreadthFirstSearch;
import version3.plugin.DuplicateEliminationPlugin;
import version3.plugin.MaximalDepthPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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
		clauses.addAll(findHardConstraints());
		clauses.addAll(clauses);
		return clauses;
	}

	/**
	 * Finds constraints that are true on at least one example
	 * // TODO Prune soft constraints?
	 * @param clauses	The hard constraints that are used as background knowledge
	 * @return	A list of soft constraints
	 */
	public List<StatusClause> findSoftConstraints(Collection<StatusClause> clauses) {
		ExecutorService service = Executors.newFixedThreadPool(2);
		List<Formula> constraints = clauses.stream().map(new StatusClauseConverter()).collect(Collectors.toList());
		Configuration newConfig = getConfiguration().addBackgroundTheory(new InlineTheory(constraints));
		List<Future<List<StatusClause>>> result = new ArrayList<>();
		for(Configuration config : newConfig.split())
			result.add(service.submit(() -> run(config)));
		List<StatusClause> softClauses = new ArrayList<>();
		try {
			for(Future<List<StatusClause>> future : result)
				softClauses.addAll(future.get());
		} catch(InterruptedException | ExecutionException e) {
			throw new IllegalStateException(e);
		} finally {
			service.shutdownNow();
		}
		return softClauses;
	}

	private List<StatusClause> run(Configuration configuration) {
		int variableCount = configuration.getVariableCount();
		LogicBase logicBase = configuration.getLogicBase();
		Vector<Theory> background = configuration.getBackgroundTheories();
		VariableRefinement refinement = new VariableRefinement(logicBase, variableCount, getExecutor(), background);
		List<StatusClause> initialNodes = Arrays.asList(new StatusClause());
		SearchAlgorithm<StatusClause> algorithm = new BreadthFirstSearch<>(refinement, StopCriterion.empty(), refinement);
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
