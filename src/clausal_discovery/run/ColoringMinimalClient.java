package clausal_discovery.run;

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
		new RunClient().run(new Configuration.FullFileConfiguration("coloring_minimal", 4, 4));
	}
}
