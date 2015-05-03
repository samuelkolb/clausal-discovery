package clausal_discovery.configuration;

import basic.FileUtil;
import clausal_discovery.core.LogicBase;
import clausal_discovery.validity.ValidatedClause;
import idp.FileManager;
import logic.theory.FileTheory;
import logic.theory.Theory;
import parse.LogicParser;
import parse.ParseException;
import vector.Vector;
import version3.algorithm.SearchAlgorithm;
import version3.plugin.CountingPlugin;
import version3.plugin.FileLoggingPlugin;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Holds all information to steer a clausal discovery search
 *
 * @author Samuel Kolb
 */
public class Configuration {

	static final FileManager FILE_MANAGER = new FileManager("log");

	// IVAR countingPlugin - The last used counting plugin

	private CountingPlugin<ValidatedClause> countingPlugin;

	public CountingPlugin<ValidatedClause> getCountingPlugin() {
		return countingPlugin;
	}

	// IVAR logicBase - The logic base to search in

	private final LogicBase logicBase;

	public LogicBase getLogicBase() {
		return logicBase;
	}

	// IVAR backgroundTheories - Background theories

	private final Vector<Theory> backgroundTheories;

	public Vector<Theory> getBackgroundTheories() {
		return backgroundTheories;
	}

	// IVAR variableCount - The maximum number of variables that can occur in a clause

	private final int variableCount;

	public int getVariableCount() {
		return variableCount;
	}

	// IVAR clauseLength - The maximum number of terms in a clause

	private final int clauseLength;

	public int getClauseLength() {
		return clauseLength;
	}

	Configuration(LogicBase logicBase, Vector<Theory> background, int variableCount, int clauseLength) {
		this.logicBase = logicBase;
		this.backgroundTheories = background;
		this.variableCount = variableCount;
		this.clauseLength = clauseLength;
	}

	/**
	 * Add plugins to monitor the algorithms execution
	 * @param algorithm	The search algorithm
	 */
	public void addPlugins(SearchAlgorithm<ValidatedClause> algorithm) {
		this.countingPlugin = new CountingPlugin<>();
		algorithm.addPlugin(getCountingPlugin());
		algorithm.addPlugin(new FileLoggingPlugin<>(FILE_MANAGER.createRandomFile("txt")));
	}

	/**
	 * Splits the configuration into multiple configurations, one for every example
	 * @return	A list of configurations
	 */
	public List<Configuration> split() {
		return getLogicBase().split().stream()
				.map(b -> new Configuration(b, getBackgroundTheories(), getVariableCount(), getClauseLength()))
				.collect(Collectors.toList());
	}

	/**
	 * Returns a copy of this configuration containing the given background theory
	 * @param theory	The background theory to add
	 * @return 	The new configuration
	 */
	public Configuration addBackgroundTheory(Theory theory) {
		Vector<Theory> backgroundTheories = getBackgroundTheories().grow(theory);
		return new Configuration(getLogicBase(), backgroundTheories, getVariableCount(), getClauseLength());
	}

	/**
	 * Create a configuration from a local file (in the examples directory)
	 * @param name			The name of the file (without extension)
	 * @param variableCount	The maximum number of variables that can occur in a clause
	 * @param clauseLength	The maximum number of terms in a clause
	 * @return	A configuration whose logic base and background theories have been read from file
	 * @throws ParseException	Iff a parsing exception occurs while parsing the logic file
	 */
	public static Configuration fromLocalFile(String name, int variableCount, int clauseLength) throws ParseException {
		LogicBase logicBase = new LogicParser().parseLocalFile(name + ".logic");
		URL url = Configuration.class.getResource("/examples/" + name + ".background");
		Vector<Theory> background = url == null
				? new Vector<>()
				: new Vector<>(new FileTheory(FileUtil.getLocalFile(url)));
		return new Configuration(logicBase, background, variableCount, clauseLength);
	}
}
