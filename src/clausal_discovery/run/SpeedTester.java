package clausal_discovery.run;

import basic.MathUtil;
import basic.StringUtil;
import log.Log;
import time.Stopwatch;
import util.Statistics;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by samuelkolb on 31/05/15.
 *
 * @author Samuel Kolb
 */
public class SpeedTester {

	protected interface Lambda {

		void execute();
	}

	protected static List<Statistics> test(List<Lambda> suppliers, int runs) {
		return suppliers.stream().map(s -> test(s, runs)).collect(Collectors.toList());
	}

	/**
	 * Runs the provided function multiple times, measuring its running time
	 * @param function	The function to be run
	 * @param runs		The number of times to run the function
	 * @return	A statistics object providing statistical access to the time data
	 */
	public static Statistics test(Lambda function, int runs) {
		double[] data = new double[runs];
		for(int i = 0; i < runs; i++) {
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			function.execute();
			data[i] = stopwatch.stop();
		}
		return new Statistics(data);
	}

	public static void main(String[] args) {
		//*
		/*print(test(Arrays.asList(
				() -> ColoringMinimalClient.main(args),
				() -> SudokuClient.main(args)
		), 8));
		/*
		print(test(() -> ColoringMinimalClient.main(args), 1));
		/*/
		/*print(test(Arrays.asList(
				() -> HousingOptimizationClient.main(new String[]{})
		), 8));/**/

	}

	protected static void print(Statistics statistics) {
		print(Collections.singletonList(statistics));
	}

	protected static void print(List<Statistics> list) {
		for(Statistics statistics : list)
			Log.LOG.printLine(Arrays.toString(statistics.getData()));

		Log.LOG.newLine().formatLine("Size | Mean | SDev");
		for(Statistics statistics : list) {
			String size = StringUtil.frontPad("" + statistics.getSize(), ' ', 4);
			String mean = String.format("%.3f", MathUtil.round(statistics.getMean() / 1000, 3));
			String sDev = String.format("%.3f", MathUtil.round(statistics.getStdDev() / 1000, 3));
			Log.LOG.formatLine("%s | %s | %s", size, mean, sDev);
		}
	}
}
