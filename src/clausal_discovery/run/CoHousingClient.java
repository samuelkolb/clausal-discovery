package clausal_discovery.run;

import clausal_discovery.configuration.Configuration;

/**
 * Created by samuelkolb on 13/04/15.
 *
 * @author Samuel Kolb
 */
public class CoHousingClient {

	/**
	 * Run the housing smart example
	 * @param args	Ignored command line arguments
	 */
	public static void main(String[] args) {
		new RunClient().run(Configuration.fromLocalFile("cohousing", 4, 4));
	}
}
