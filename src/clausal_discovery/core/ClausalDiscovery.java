package clausal_discovery.core;

import clausal_discovery.configuration.Configuration;
import clausal_discovery.instance.InstanceList;
import clausal_discovery.validity.ValidatedClause;
import logic.expression.formula.Formula;
import logic.theory.InlineTheory;
import logic.theory.Theory;
import vector.SafeList;
import vector.Vector;
import version3.algorithm.*;
import version3.algorithm.implementation.BreadthFirstSearch;
import version3.plugin.DuplicateEliminationPlugin;
import version3.plugin.MaximalDepthPlugin;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The clausal discovery class sets up the search
 *
 * @author Samuel Kolb
 */
public class ClausalDiscovery {

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
	public List<ValidatedClause> findHardConstraints() {
		return run(getConfiguration(), ValidatedClause::coversAll);
	}

	/**
	 * Use the given configuration to find constraints that hold on a certain percentage of the examples
	 * @param support	The percentage (between 0 and 1) of examples, that a constraint needs to hold on
	 * @return	A list of status clauses that hold on at least one example
	 */
	public List<ValidatedClause> findSoftConstraints(double support) {
		double size = Math.ceil(getConfiguration().getLogicBase().getExamples().size() * support);
		return run(getConfiguration(), c -> c.getSupportCount() >= size);
	}

	/**
	 * Finds constraints that are true on at least one example
	 * @param clauses	The hard constraints that are used as background knowledge
	 * @return	A list of soft constraints
	 */
	public List<ValidatedClause> findSoftConstraints(Collection<ValidatedClause> clauses) {
		List<Formula> constraints = clauses.stream()
				.map(ValidatedClause::getClause)
				.map(new StatusClauseConverter())
				.collect(Collectors.toList());
		Configuration newConfig = getConfiguration().addBackgroundTheory(new InlineTheory(constraints));
		return run(newConfig, c -> c.getSupportCount() > 0);
	}

	private List<ValidatedClause> run(Configuration configuration, Predicate<ValidatedClause> validityTest) {
		int variables = configuration.getVariableCount();
		LogicBase logicBase = configuration.getLogicBase();
		SafeList<Theory> background = configuration.getBackgroundTheories();
		InstanceList list = new InstanceList(logicBase.getSearchList(), variables);
		VariableRefinement refinement = new VariableRefinement(logicBase, list, background, validityTest);
		List<ValidatedClause> initialNodes = Collections.singletonList(new ValidatedClause(list, logicBase));
		SearchAlgorithm<ValidatedClause> algorithm = new BreadthFirstSearch<>(refinement, StopCriterion.empty(), refinement);
		configuration.addPlugins(algorithm);
		algorithm.addPlugin(new MaximalDepthPlugin<>(configuration.getClauseLength()));
		algorithm.addPlugin(new DuplicateEliminationPlugin<>(false));
		algorithm.addPlugin(refinement);

		List<ValidatedClause> clauses = algorithm.search(initialNodes).getSolutions();
		this.excessTime = refinement.getExcessTimer().stop();
		return clauses;
	}
}
