package clausal_discovery.run;

import clausal_discovery.configuration.Configuration;
import clausal_discovery.core.ClausalDiscovery;
import clausal_discovery.core.ClausalOptimization;
import clausal_discovery.core.StatusClause;
import log.Log;
import parse.ParseException;

import java.util.List;

/**
 * Created by samuelkolb on 13/04/15.
 *
 * @author Samuel Kolb
 */
public class HousingOptimizationClient {

	static {
		Log.LOG.addMessageFilter(message -> (message.MESSAGE == null || !message.MESSAGE.startsWith("INFO")));
	}

	/**
	 * Run the housing smart example
	 * @param args	Ignored command line arguments
	 */
	public static void main(String[] args) {
		try {
			Configuration configuration = Configuration.fromLocalFile("housing_opt", 4, 3);
			new ClausalOptimization(configuration).run();
		} catch(ParseException e) {
			Log.LOG.on().printLine("Error occurred while parsing " + "housing_opt.logic").printLine(e.getMessage());
		}
	}
}
