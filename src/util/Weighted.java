package util;

import logic.expression.formula.Formula;

/**
 * Represents a weighted formula
 *
 * @author Samuel Kolb
 */
public class Weighted<T> {

	private Double weight;

	public Double getWeight() {
		return weight;
	}

	private T object;

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
