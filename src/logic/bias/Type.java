package logic.bias;

import java.util.Optional;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public class Type {

	private static class GenericType extends Type {

		private GenericType() {
			super("Generic", Optional.empty());
		}

		@Override
		public boolean isSuperTypeOf(Type type) {
			return true;
		}
	}

	private static class BuiltInType extends Type {

		private BuiltInType(String name) {
			super(name);
		}

		@Override
		public boolean isBuiltIn() {
			return true;
		}
	}

	//region Variables
	public static final Type GENERIC = new GenericType();

	private final String name;

	public String getName() {
		return name;
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

	/**
	 * Create a new type without parent
	 * @param name	The name of the new type
	 */
	public Type(String name) {
		this(name, Optional.empty());
		if(name.equals("Generic"))
			throw new IllegalArgumentException("Generic is a reserved name");
	}

	private Type(String name, Optional<Type> parent) {
		this.name = name;
		this.parent = parent;
	}

	//endregion

	//region Public methods

	/**
	 * Create a subtype whose parent type will be this tye
	 * @param name	The name of the new type
	 * @return	A new type where <code>return.getName().equals(name)</code> and <code>return.getParent() == this</code>
	 */
	public Type getSubtype(String name) {
		return new Type(name, Optional.of(this));
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

	public boolean isBuiltIn() {
		return false;
	}

	/**
	 * Create a built-in type
	 * @param name	The name of the built in type
	 * @return	A built-in type
	 */
	public static Type createBuiltIn(String name) {
		return new BuiltInType(name);
	}

	//endregion
}
