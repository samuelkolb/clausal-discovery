package clausal_discovery.run;

import basic.MathUtil;
import clausal_discovery.configuration.Configuration;
import clausal_discovery.core.ClausalDiscovery;
import clausal_discovery.validity.ValidatedClause;
import idp.IdpExecutor;
import log.LinkTransformer;
import log.Log;
import time.Stopwatch;

import java.util.List;

/**
 * Created by samuelkolb on 13/04/15.
 *
 * @author Samuel Kolb
 */
public class RunClient {

	static {
		Log.LOG.addMessageFilter(message -> (message.MESSAGE == null || !message.MESSAGE.startsWith("INFO")));
		Log.LOG.addTransformer(new LinkTransformer());
	}

	/**
	 * Run clausal discovery with the given configuration
	 * @param configuration	The run configuration
	 * @return	The result clauses
	 */
	public List<ValidatedClause> run(Configuration configuration) {
		ClausalDiscovery clausalDiscovery = new ClausalDiscovery(configuration);
		Stopwatch stopwatch = new Stopwatch(true);
		List<ValidatedClause> clauses = null;
		try {
			clauses = clausalDiscovery.findHardConstraints();
			Log.LOG.printLine("\nSearch finished in " + round(stopwatch.stop()) + "s:");
			for(int i = 0; i < clauses.size(); i++)
				Log.LOG.printLine("\t" + (i + 1) + ": " + clauses.get(i));
		} catch(Exception e) {
			Log.LOG.printTitle("Exception occurred");
			System.out.flush();
			System.err.flush();
			e.printStackTrace(System.err);
		}

		IdpExecutor executor = IdpExecutor.get();
		double time = round(executor.entailmentStopwatch.stop());

		Log.LOG.newLine().printLine("Selected " + configuration.getCountingPlugin().getSelectedCount() + " nodes");
		Log.LOG.printLine("Processed " + configuration.getCountingPlugin().getProcessedCount() + " nodes");
		Log.LOG.printLine("Expanded " + configuration.getCountingPlugin().getExpandedCount() + " nodes");

		Log.LOG.newLine().printLine(executor.entailmentCount + " entailment checks took " + time + "s.");
		Log.LOG.printLine("Pruning time " + round(clausalDiscovery.getExcessTime()) + "s.");

		return clauses;
	}

	private double round(double time) {
		return MathUtil.round(time / 1000, 3);
	}
}
