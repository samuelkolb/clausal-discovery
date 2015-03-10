package logic.theory;

import vector.Vector;
import logic.bias.Type;
import logic.expression.formula.Predicate;

/**
 * Created by samuelkolb on 09/11/14.
 */
public class Structure {

	public static abstract class StructureElement {

		/**
		 * Prints the structure element
		 * @return	A string representing this structure element
		 */
		public abstract String print();
	}

	public static abstract class TypeElement extends StructureElement {

		private final Type type;

		public Type getType() {
			return type;
		}

		protected TypeElement(Type type) {
			this.type = type;
		}
	}

	public static abstract class PredicateElement extends StructureElement {

		private final Predicate predicate;

		public Predicate getPredicate() {
			return predicate;
		}

		protected PredicateElement(Predicate predicate) {
			this.predicate = predicate;
		}
	}

	private final Vector<TypeElement> typeElements;

	public Vector<TypeElement> getTypeElements() {
		return typeElements;
	}

	private final Vector<PredicateElement> predicateElements;

	public Vector<PredicateElement> getPredicateElements() {
		return predicateElements;
	}

	private final boolean isPositive;

	public boolean isPositive() {
		return isPositive;
	}

	/**
	 * Creates a new structure using the given constants and predicates
	 * @param typeElements      The given type structures
	 * @param predicateElements	The given predicate instances
	 * @param isPositive		Whether this structure is a positive example or not
	 */
	public Structure(Vector<TypeElement> typeElements, Vector<PredicateElement> predicateElements, boolean isPositive) {
		this.typeElements = typeElements;
		this.predicateElements = predicateElements;
		this.isPositive = isPositive;
	}
}
