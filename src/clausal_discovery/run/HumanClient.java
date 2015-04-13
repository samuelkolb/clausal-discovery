package clausal_discovery.run;

/**
 * Created by samuelkolb on 13/04/15.
 *
 * @author Samuel Kolb
 */
public class HumanClient {

	/**
	 * Run the human example
	 * @param args	Ignored command line arguments
	 */
	public static void main(String[] args) {
		new RunClient().run(new Configuration.FullFileConfiguration("human", 1, 6));
	}
}
