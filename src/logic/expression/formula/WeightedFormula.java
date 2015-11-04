package logic.expression.formula;

/**
 * Represents a weighted formula
 *
 * @author Samuel Kolb
 */
public class WeightedFormula {

	private Double weight;

	public Double getWeight() {
		return weight;
	}

	private Formula formula;

	public Formula getFormula() {
		return formula;
	}

	/**
	 * Creates a weighted formula
	 * @param weight	The weight
	 * @param formula	The formula
	 */
	public WeightedFormula(Double weight, Formula formula) {
		this.weight = weight;
		this.formula = formula;
	}
}
