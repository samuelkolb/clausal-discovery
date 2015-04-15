package logic.bias;

import java.util.Optional;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public class Type {

	//region Variables
	public static final Type INT = createBuiltIn("int");
	public static final Type UNDEFINED = createBuiltIn("Undefined");

	private final String name;

	public String getName() {
		return name;
	}

	private final boolean builtIn;

	public boolean isBuiltIn() {
		return builtIn;
	}

	private final Optional<Type> parent;

	/**
	 * Returns whether this type has a parent type
	 * @return	True iff this type has a parent type
	 */
	public boolean hasParent() {
		return parent.isPresent();
	}

	public Type getParent() {
		if(!hasParent())
			throw new IllegalStateException();
		return parent.get();
	}

	//endregion

	//region Construction

	//endregion

	//region Public methods

	/**
	 * Create a new type without parent
	 * @param name	The name of the new type
	 */
	public Type(String name) {
		this(name, false, Optional.of(UNDEFINED));
	}

	private Type(String name, boolean builtIn, Optional<Type> parent) {
		this.name = name;
		this.builtIn = builtIn;
		this.parent = parent;
	}

	/**
	 * Create a subtype whose parent type will be this tye
	 * @param name	The name of the new type
	 * @return	A new type where <code>return.getName().equals(name)</code> and <code>return.getParent() == this</code>
	 */
	public Type getSubtype(String name) {
		return new Type(name, false, Optional.of(this));
	}

	/**
	 * Determines whether this type is a super type of the given type
	 * @param type	The potential subtype
	 * @return	True if the given type is a subtype of this type
	 */
	public boolean isSuperTypeOf(Type type) {
		return type.equals(this) || type.hasParent() && isSuperTypeOf(type.getParent());
	}

	@Override
	public String toString() {
		return getName();
	}

	protected static Type createBuiltIn(String name) {
		return new Type(name, true, Optional.empty());
	}

	//endregion
}
