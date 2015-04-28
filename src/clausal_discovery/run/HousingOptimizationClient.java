package clausal_discovery.run;

import basic.ArrayUtil;
import clausal_discovery.configuration.Configuration;
import clausal_discovery.core.*;
import log.Log;
import parse.ParseException;
import parse.PreferenceParser;
import util.Numbers;
import vector.Vector;

import java.util.Arrays;
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
			PreferenceParser preferenceParser = new PreferenceParser(configuration.getLogicBase().getExamples());
			Preferences preferences = preferenceParser.parseLocalFile("housing_opt.logic");
			ScoringFunction function = new ClausalOptimization(configuration).run(preferences);

		} catch(ParseException e) {
			Log.LOG.on().printLine("Error occurred while parsing " + "housing_opt.logic").printLine(e.getMessage());
		}
	}
}
