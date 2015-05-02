package clausal_discovery.run;

import clausal_discovery.configuration.Configuration;
import clausal_discovery.core.ClausalOptimization;
import clausal_discovery.core.Preferences;
import log.LinkTransformer;
import log.Log;
import parse.ParseException;
import parse.PreferenceParser;

/**
 * Created by samuelkolb on 13/04/15.
 *
 * @author Samuel Kolb
 */
public class HousingOptimizationClient {

	static {
		Log.LOG.addMessageFilter(message -> (message.MESSAGE == null || !message.MESSAGE.startsWith("INFO")));
		Log.LOG.addTransformer(new LinkTransformer());
	}

	/**
	 * Run the housing smart example
	 * @param args	Ignored command line arguments
	 */
	public static void main(String[] args) {
		try {
			Configuration configuration = Configuration.fromLocalFile("housing_opt", 4, 3);
			PreferenceParser preferenceParser = new PreferenceParser(configuration.getLogicBase().getExamples());
			Preferences preferences = preferenceParser.parseLocalFile("housing_opt.logic");
			new ClausalOptimization(configuration).run(preferences);

		} catch(ParseException e) {
			Log.LOG.on().printLine("Error occurred while parsing " + "housing_opt.logic").printLine(e.getMessage());
		}
	}
}
