package clausal_discovery.run;

import log.Log;
import util.Randomness;

/**
 * This class offers batch evaluation for optimization problems
 *
 * @author Samuel Kolb
 */
public class OptimizationBatchTester {

	public static final double STEP = 0.1;

	//region Variables

	private int minimumSplit = 2;

	protected OptimizationBatchTester setMaximumSplit(int maximumSplit) {
		this.maximumSplit = maximumSplit;
		return this;
	}

	private int maximumSplit = 8;

	protected OptimizationBatchTester setMinimumSplit(int minimumSplit) {
		this.minimumSplit = minimumSplit;
		return this;
	}

	private int minimumFraction = 1;

	protected OptimizationBatchTester setMinimumFraction(int minimumFraction) {
		this.minimumFraction = minimumFraction;
		return this;
	}

	private int maximumFraction = 10;

	protected OptimizationBatchTester setMaximumFraction(int maximumFraction) {
		this.maximumFraction = maximumFraction;
		return this;
	}

	private int minimumNoise = 0;

	protected OptimizationBatchTester setMinimumNoise(int minimumNoise) {
		this.minimumNoise = minimumNoise;
		return this;
	}

	private int maximumNoise = 5;

	protected OptimizationBatchTester setMaximumNoise(int maximumNoise) {
		this.maximumNoise = maximumNoise;
		return this;
	}

	private final OptimizationTestClient client;

	//endregion

	//region Construction

	protected OptimizationBatchTester(OptimizationTestClient client) {
		this.client = client;
	}

	//endregion

	//region Public methods

	@SuppressWarnings("unused")
	protected OptimizationBatchTester fixSplit(int split) {
		setMinimumSplit(split);
		setMaximumSplit(split);
		return this;
	}

	@SuppressWarnings("unused")
	protected OptimizationBatchTester fixFraction(int fraction) {
		setMinimumFraction(fraction);
		setMaximumFraction(fraction);
		return this;
	}

	@SuppressWarnings("unused")
	protected OptimizationBatchTester fixNoise(int noise) {
		setMinimumNoise(noise);
		setMaximumNoise(noise);
		return this;
	}

	@SuppressWarnings("unused")
	protected void selfTest() {
		Log.LOG.printLine("Seed: " + Randomness.getSeed());
		Log.LOG.printLine("size  | noise | score");
		Log.LOG.printLine("- - - | - - - | - - -");
		Log.LOG.saveState().off();
		clausal_discovery.test.OptimizationTester tester = client.getTester();
		for(int size = minimumFraction; size <= maximumFraction; size++)
			for(int noise = minimumNoise; noise <= maximumNoise; noise++) {
				double fractionSize = size * STEP;
				double fractionNoise = noise * STEP;
				double score = client.score(tester, fractionSize, fractionNoise);
				Log.LOG.on().formatLine("%.3f | %.3f | %.3f", fractionSize, fractionNoise, score).off();
			}
		Log.LOG.revert();
	}

	@SuppressWarnings("unused")
	protected void splitTest() {
		Log.LOG.printLine("Seed: " + Randomness.getSeed());
		Log.LOG.printLine("split | size  | noise | score");
		Log.LOG.printLine("- - - | - - - | - - - | - - -");
		Log.LOG.saveState().off();
		for(int split = minimumSplit; split <= maximumSplit; split++) {
			double fractionSplit = split * STEP;
			clausal_discovery.test.OptimizationTester tester = client.getTester(fractionSplit);
			for(int size = minimumFraction; size <= maximumFraction; size++) {
				for(int noise = minimumNoise; noise <= maximumNoise; noise++) {
					double fractionSize = size * STEP;
					double fractionNoise = noise * STEP;
					double score = client.score(tester, fractionSize, fractionNoise);
					Log.LOG.on().formatLine("%.3f | %.3f | %.3f | %.3f",
							fractionSplit, fractionSize, fractionNoise, score).off();
				}
			}
		}
		Log.LOG.revert();
	}
	//endregion
}
