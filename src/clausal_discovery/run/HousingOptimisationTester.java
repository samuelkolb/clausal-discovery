package clausal_discovery.run;

import log.Log;
import log.RelativeTimeTransformer;

/**
 * Created by samuelkolb on 01/05/15.
 *
 * @author Samuel Kolb
 */
public class HousingOptimisationTester {

	static {
		Log.LOG.addMessageFilter(message -> (message.MESSAGE == null || !message.MESSAGE.startsWith("INFO")));
		Log.LOG.addTransformer(new RelativeTimeTransformer());
	}

	/**
	 * Run the housing smart example
	 * @param args	Ignored command line arguments
	 */
	public static void main(String[] args) {
		String name = "housing_opt_small";
		OptimizationTestClient client = new OptimizationTestClient(name, name + "_1", 4, 3);
		OptimizationBatchTester batchTestClient = new OptimizationBatchTester(client);
		batchTestClient.fixNoise(0);
		for(int i = 0; i < 5; i++) {
			Log.LOG.newLine();
			batchTestClient.splitTest();
		}
	}

}
