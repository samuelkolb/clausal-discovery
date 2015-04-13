package clausal_discovery.run;

/**
 * Created by samuelkolb on 13/04/15.
 *
 * @author Samuel Kolb
 */
public class ColoringOptimalClient {

	/**
	 * Run the coloring optimal example
	 * @param args	Ignored command line arguments
	 */
	public static void main(String[] args) {
		new RunClient().run(new Configuration.FullFileConfiguration("coloring_optimal", 4, 4));
	}
}
