package clausal_discovery.run;

import basic.FileUtil;
import clausal_discovery.core.LogicBase;
import clausal_discovery.core.StatusClause;
import idp.FileManager;
import logic.parse.LogicParser;
import version3.algorithm.SearchAlgorithm;
import version3.plugin.CountingPlugin;
import version3.plugin.FileLoggingPlugin;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by samuelkolb on 13/04/15.
 *
 * @author Samuel Kolb
 */
public abstract class Configuration {

	public static abstract class FileConfiguration extends Configuration {

		private final LogicBase logicBase;

		@Override
		public LogicBase getLogicBase() {
			return logicBase;
		}

		private final Optional<String> backgroundFile;

		@Override
		public Optional<String> getBackgroundFile() {
			return backgroundFile;
		}

		protected FileConfiguration(String name) {
			this.logicBase = new LogicParser().readLocalFile(name + ".logic");
			URL url = Configuration.class.getResource("/examples/" + name + ".background");
			this.backgroundFile = url == null
					? Optional.empty()
					: Optional.of(FileUtil.getLocalFile(url).getAbsolutePath());
		}
	}

	public static class FullFileConfiguration extends FileConfiguration {

		private final int variableCount;

		@Override
		public int getVariableCount() {
			return variableCount;
		}

		private final int clauseLength;

		@Override
		public int getClauseLength() {
			return clauseLength;
		}

		/**
		 * Creates a new full file configuration
		 * @param name			The name of the logic file residing in the examples directory
		 * @param variableCount	The number of variables to use
		 * @param clauseLength	The maximal length of clauses
		 */
		public FullFileConfiguration(String name, int variableCount, int clauseLength) {
			super(name);
			this.variableCount = variableCount;
			this.clauseLength = clauseLength;
		}
	}

	static final FileManager FILE_MANAGER = new FileManager("log");

	// IVAR countingPlugin - The last used counting plugin

	private CountingPlugin<StatusClause> countingPlugin;

	public CountingPlugin<StatusClause> getCountingPlugin() {
		return countingPlugin;
	}

	/**
	 * Add plugins to monitor the algorithms execution
	 * @param algorithm	The search algorithm
	 */
	public void addPlugins(SearchAlgorithm<StatusClause> algorithm) {
		this.countingPlugin = new CountingPlugin<>();
		algorithm.addPlugin(getCountingPlugin());
		algorithm.addPlugin(new FileLoggingPlugin<>(FILE_MANAGER.createRandomFile("txt")));
	}

	public abstract LogicBase getLogicBase();

	public abstract Optional<String> getBackgroundFile();

	public abstract int getVariableCount();

	public abstract int getClauseLength();
}
