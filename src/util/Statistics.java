package util;

/**
 * Packages data and offers statistical access
 *
 * @author Samuel Kolb
 */
public class Statistics {

	//region Variables
	private double[] data;

	public double[] getData() {
		double[] array = new double[data.length];
		System.arraycopy(data, 0, array, 0, data.length);
		return array;
	}

	private Double mean = null;

	public double getMean() {
		if(mean == null)
			mean = calculateMean();
		return mean;
	}

	private Double var = null;

	public double getVar() {
		if(var == null)
			var = calculateVar();
		return var;
	}

	private Double stdDev = null;

	public double getStdDev() {
		if(stdDev == null)
			stdDev = calculateStdDev();
		return stdDev;
	}

	private Double max = null;

	public Double getMax() {
		if(max == null)
			max = calculateMax();
		return max;
	}

	private Double min = null;

	public Double getMin() {
		if(min == null)
			min = calculateMin();
		return min;
	}

	private Double sum = null;

	public Double getSum() {
		if(sum == null)
			sum = calculateSum();
		return sum;
	}

	//endregion

	//region Construction

	/**
	 * Creates a new statistics object
	 * @param data	The data
	 */
	public Statistics(double[] data) {
		this.data = new double[data.length];
		System.arraycopy(data, 0, this.data, 0, data.length);
	}

	//endregion

	/**
	 * Returns the maximum value in the array a[], -infinity if no such value.
	 */
	private double calculateMax() {
		double max = Double.NEGATIVE_INFINITY;
		for(double aData : data) {
			if(Double.isNaN(aData))
				return Double.NaN;
			if(aData > max)
				max = aData;
		}
		return max;
	}

	/**
	 * Returns the minimum value in the array a[], +infinity if no such value.
	 */
	private double calculateMin() {
		double min = Double.POSITIVE_INFINITY;
		for(double aData : data) {
			if(Double.isNaN(aData))
				return Double.NaN;
			if(aData < min)
				min = aData;
		}
		return min;
	}

	/**
	 * Returns the average value in the array a[], NaN if no such value.
	 */
	private double calculateMean() {
		if (data.length == 0)
			return Double.NaN;
		double sum = getSum();
		return sum / data.length;
	}

	/**
	 * Returns the population variance in the array a[], NaN if no such value.
	 */
	private double calculateVar() {
		if(data.length == 0)
			return Double.NaN;
		double avg = getMean();
		double sum = 0.0;
		for(double d : data) {
			sum += (d - avg) * (d - avg);
		}
		return sum / data.length;
	}

	/**
	 * Returns the population standard deviation in the array a[], NaN if no such value.
	 */
	private double calculateStdDev() {
		return Math.sqrt(getVar());
	}

	/**
	 * Returns the sum of all values in the array a[].
	 */
	private double calculateSum() {
		double sum = 0.0;
		for(double d : data)
			sum += d;
		return sum;
	}


}