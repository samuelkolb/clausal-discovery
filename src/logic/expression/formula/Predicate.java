package logic.expression.formula;

import basic.ArrayUtil;
import basic.StringUtil;
import vector.Vector;
import logic.bias.Mode;
import logic.bias.Type;
import logic.expression.term.Term;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public class Predicate {

	//region Variables
	private final String name;

	public String getName() {
		return name;
	}

	private final Vector<Type> types;

	public Vector<Type> getTypes() {
		return types;
	}

	//endregion

	//region Construction

	/**
	 * Creates a new predicate with the given name and arity
	 * The modes and types of the parameters will be undefined
	 * @param name	The predicate name
	 * @param arity	The arity (number of parameters) of the predicate
	 */
	public Predicate(String name, int arity) {
		this(name, ArrayUtil.fill(new Mode[arity], Mode.UNDEFINED));
	}

	/**
	 * Creates a new predicate with the given name and mode declaration
	 * The types of the parameters will be undefined
	 * @param name	The predicate name
	 * @param modes	The modes of the parameters
	 */
	public Predicate(String name, Mode... modes) {
		this(name, modes, ArrayUtil.fill(new Type[modes.length], Type.UNDEFINED));
	}

	/**
	 * Creates a new predicate with the given name and type declaration
	 * The modes of the parameters will be undefined
	 * @param name	The predicate name
	 * @param types	The types of the parameters
	 */
	public Predicate(String name, Type... types) {
		this(name, ArrayUtil.fill(new Mode[types.length], Mode.UNDEFINED), types);
	}

	/**
	 * Creates a new predicate with the given name, mode- and type declaration
	 * @param name	The predicate name
	 * @param modes	The modes of the parameters
	 * @param types	The types of the parameters
	 */
	public Predicate(String name, Mode[] modes, Type[] types) {
		if(modes.length != types.length)
			throw new IllegalArgumentException("The number of modes differed from the numbers of types.");
		this.name = name;
		this.types = ArrayUtil.wrap(types);
	}

	//endregion

	//region Public methods

	/**
	 * Creates and returns an instance of this predicate
	 * @param terms The terms to instantiate the predicate with<br />
	 *              The number of terms must match the arity of this predicate
	 * @return  An instance of this predicate
	 */
	public PredicateInstance getInstance(Term... terms) {
		return new PredicateInstance(this, terms);
	}

	public int getArity() {
		return types.length;
	}

	@Override
	public String toString() {
		return getName() + "(" + StringUtil.join(", ", getTypes().getArray()) + ")";
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Predicate predicate = (Predicate) o;

		return name.equals(predicate.name) && types.equals(predicate.types);

	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + types.hashCode();
		return result;
	}

	//endregion
}
