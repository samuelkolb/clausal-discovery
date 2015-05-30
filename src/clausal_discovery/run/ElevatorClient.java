package clausal_discovery.run;

import clausal_discovery.configuration.Configuration;
import clausal_discovery.core.ClausalDiscovery;
import clausal_discovery.validity.ValidatedClause;
import log.Log;
import log.PrefixFilter;

import java.util.List;

/**
 * Created by samuelkolb on 13/04/15.
 *
 * @author Samuel Kolb
 */
public class ElevatorClient {

	/**
	 * Run the human example
	 * @param args	Ignored command line arguments
	 */
	public static void main(String[] args) {
		Log.LOG.addMessageFilter(PrefixFilter.ignore("INFO"));
		Configuration configuration = Configuration.fromLocalFile("elevator", 4, 3);
		ClausalDiscovery clausalDiscovery = new ClausalDiscovery(configuration);
		List<ValidatedClause> clauses = clausalDiscovery.findSoftConstraints(0.1);
		Log.LOG.newLine();
		clauses.forEach(vc -> Log.LOG.formatLine("%.2f: %s", vc.getValidCount() / 3.0, vc));
	}
}
