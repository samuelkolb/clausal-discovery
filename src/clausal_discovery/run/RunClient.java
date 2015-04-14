package clausal_discovery.run;

import basic.MathUtil;
import clausal_discovery.core.ClausalDiscovery;
import clausal_discovery.core.StatusClause;
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
		Log.LOG.addMessageFilter(message -> !message.MESSAGE.startsWith("INFO"));
		Log.LOG.addTransformer(new LinkTransformer());
	}

	//region Variables

	//endregion

	//region Construction

	//endregion

	//region Public methods

	/**
	 * Run clausal discovery with the given configuration
	 * @param configuration	The run configuration
	 */
	public void run(Configuration configuration) {
		ClausalDiscovery clausalDiscovery = new ClausalDiscovery(configuration);
		Stopwatch stopwatch = new Stopwatch(true);
		try {
			List<StatusClause> clauses = clausalDiscovery.findConstraints();
			Log.LOG.printLine("\nSearch finished in " + round(stopwatch.stop()) + "s:");
			for(int i = 0; i < clauses.size(); i++)
				Log.LOG.printLine("\t" + (i + 1) + ": " + clauses.get(i));
		} catch(Exception e) {
			Log.LOG.printTitle("Exception occurred");
			System.out.flush();
			System.err.flush();
			e.printStackTrace(System.err);
		}

		IdpExecutor executor = clausalDiscovery.getExecutor();
		double time = round(executor.entailmentStopwatch.stop());
		Log.LOG.newLine().printLine(executor.entailmentCount + " entailment checks took " + time + "s.");
		Log.LOG.printLine("Entailment checks excess time " + round(clausalDiscovery.getExcessTime()) + "s.");
	}

	//endregion

	// region Private methods

	private double round(double time) {
		return MathUtil.round(time / 1000, 3);
	}

	// endregion
}
