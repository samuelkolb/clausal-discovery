package util;

/**
 * Represents a weighted formula
 *
 * @author Samuel Kolb
 */
public class Weighted<T> {

	private final Double weight;

	public Double getWeight() {
		return weight;
	}

	private final T object;

	public T getObject() {
		return object;
	}

	/**
	 * Creates a weighted object
	 *
	 * @param weight The weight
	 * @param object The object
	 */
	public Weighted(Double weight, T object) {
		this.weight = weight;
		this.object = object;
	}

	public boolean isRequired() {
		return weight.isInfinite() && weight > 0;
	}

	public boolean isForbidden() {
		return weight.isInfinite() && weight < 0;
	}
}
