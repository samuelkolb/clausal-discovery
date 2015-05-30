package clausal_discovery.run;

import clausal_discovery.configuration.Configuration;

/**
 * Created by samuelkolb on 13/04/15.
 *
 * @author Samuel Kolb
 */
public class ColoringMinimalClient {

	/**
	 * Run the coloring minimal example
	 * @param args	Ignored command line arguments
	 */
	public static void main(String[] args) {
		new RunClient().run(Configuration.fromLocalFile("coloring_minimal", 3, 3));
	}
}
