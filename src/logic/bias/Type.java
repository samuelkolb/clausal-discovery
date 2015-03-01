package logic.bias;

import java.util.Optional;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public class Type {

	//region Variables
	public static final Type UNDEFINED = new Type();

	private final String name;

	public String getName() {
		return name;
	}

	private final Optional<Type> parent;

	private boolean hasParent() {
		return parent.isPresent();
	}

	private Type getParent() {
		if(!hasParent())
			throw new IllegalStateException();
		return parent.get();
	}

	//endregion

	//region Construction

	//endregion

	//region Public methods

	private Type() {
		this.name = "Undefined";
		this.parent = Optional.empty();
	}

	public Type(String name) {
		this(name, UNDEFINED);
	}

	private Type(String name, Type parent) {
		if(UNDEFINED.name.equals(name))
			throw new IllegalArgumentException("The name \"Undefined\" is reserved");
		this.name = name;
		this.parent = Optional.of(parent);
	}

	public Type getSubtype(String name) {
		return new Type(name, this);
	}

	public boolean isSuperTypeOf(Type type) {
		return type.equals(this) || type.hasParent() && isSuperTypeOf(type.getParent());
	}

	@Override
	public String toString() {
		return getName();
	}

	//endregion
}
