package clausal_discovery.configuration;

import basic.FileUtil;
import clausal_discovery.core.LogicBase;
import clausal_discovery.validity.ValidatedClause;
import idp.FileManager;
import log.Log;
import logic.theory.FileTheory;
import logic.theory.Theory;
import pair.TypePair;
import parse.LogicParser;
import parse.ParseException;
import vector.Vector;
import version3.algorithm.SearchAlgorithm;
import version3.plugin.CountingPlugin;
import version3.plugin.FileLoggingPlugin;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
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
		return getLogicBase().split().stream() .map(this::copy).collect(Collectors.toList());
	}

	/**
	 * Splits the configuration into two, keeping the specified fraction of examples in the first configuration
	 * @param fraction	The fraction of examples to keep in the first configuration (between 0 and 1)
	 * @return	A pair of configurations
	 */
	public TypePair<Configuration> split(double fraction) {
		TypePair<LogicBase> logicBases = getLogicBase().split(fraction);
		return TypePair.of(copy(logicBases.getFirst()), copy(logicBases.getSecond()));
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
		LogicBase logicBase;
		try {
			logicBase = new LogicParser().parseLocalFile(name + ".logic");
		} catch(ParseException e) {
			Log.LOG.formatLine("Error parsing file %s.logic\n%s", name, e.getMessage());
			throw new IllegalArgumentException(e);
		}
		URL url = Configuration.class.getResource("/examples/" + name + ".background");
		Vector<Theory> background = url == null
				? new Vector<>()
				: new Vector<>(new FileTheory(FileUtil.getLocalFile(url)));
		return new Configuration(logicBase, background, variableCount, clauseLength);
	}

	/**
	 * Create a configuration using a logic and and optional background file.
	 * @param file				The logic file
	 * @param backgroundFile	An optional background file
	 * @param variables			The number of variables per clause
	 * @param literals			The number of literals per clause
	 * @return	A configuration object
	 * @throws ParseException	Iff a parsing exception occurs while parsing the logic file
	 */
	public static Configuration fromFile(File file, Optional<File> backgroundFile, int variables, int literals)
			throws ParseException {
		LogicBase logicBase;
		try {
			logicBase = new LogicParser().parse(FileUtil.readFile(file));
		} catch(ParseException e) {
			Log.LOG.formatLine("Error parsing file %s.logic\n%s", file.getName(), e.getMessage());
			throw new IllegalArgumentException(e);
		}
		Vector<Theory> background = backgroundFile.isPresent()
				? new Vector<>(new FileTheory(backgroundFile.get()))
				: new Vector<>();
		return new Configuration(logicBase, background, variables, literals);
	}

	/**
	 * Copies this configuration with a new logic base
	 * @param logicBase	The new logic base
	 * @return	A configuration that is an exact copy of this configuration except for the logic base
	 */
	public Configuration copy(LogicBase logicBase) {
		return new Configuration(logicBase, getBackgroundTheories(), getVariableCount(), getClauseLength());
	}
}
