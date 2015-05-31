package clausal_discovery.run;

import time.Stopwatch;
import util.Statistics;

import java.util.function.Supplier;

/**
 * Created by samuelkolb on 31/05/15.
 *
 * @author Samuel Kolb
 */
public class SpeedTester {

	/**
	 * Runs the provided function multiple times, measuring its running time
	 * @param function	The function to be run
	 * @param runs		The number of times to run the function
	 * @return	A statistics object providing statistical access to the time data
	 */
	public static Statistics test(Supplier<Void> function, int runs) {
		double[] data = new double[runs];
		for(int i = 0; i < runs; i++) {
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			function.get();
			data[i] = stopwatch.stop();
		}
		return new Statistics(data);
	}
}
