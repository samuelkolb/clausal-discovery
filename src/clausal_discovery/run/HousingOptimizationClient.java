package clausal_discovery.run;

/**
 * Created by samuelkolb on 13/04/15.
 *
 * @author Samuel Kolb
 */
public class HousingOptimizationClient {

	/**
	 * Run the housing smart example
	 * @param args	Ignored command line arguments
	 */
	public static void main(String[] args) {
		new RunClient().run(new Configuration.FullFileConfiguration("housing_opt", 4, 4));
	}
}
