package logic.expression.formula;

import vector.Vector;

import java.util.HashSet;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public abstract class CompositeFormula extends Formula {

	//region Variables
	private final Vector<Formula> elements;

	protected Vector<Formula> getElements() {
		return elements;
	}

	public int getElementCount() {
		return elements.length;
	}

	/**
	 * Returns the ith element, starting with element 0
	 * @param index	The index of the element to return
	 * @return	The element associated with the given index
	 */
	public Formula getElement(int index) {
		return getElements().get(index);
	}

	//endregion

	//region Construction

	CompositeFormula(Formula... elements) {
		this.elements = new Vector<>(elements);
	}

	//endregion

	//region Public methods

	@Override
	public boolean isGround() {
		for(Formula formula : getElements())
			if(!formula.isGround())
				return false;
		return true;
	}

	public abstract CompositeFormula instance(Formula... elements);

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		CompositeFormula that = (CompositeFormula) o;

		return new HashSet<>(elements).equals(new HashSet<Formula>(that.elements));

	}

	@Override
	public int hashCode() {
		return elements.hashCode();
	}

	//endregion
}
