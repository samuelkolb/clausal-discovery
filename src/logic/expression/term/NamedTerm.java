package logic.expression.term;

import logic.bias.Type;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public abstract class NamedTerm extends Term {

	//region Variables
	private final String name;

	public String getName() {
		return name;
	}

	//endregion

	//region Construction

	NamedTerm(String name) {
		super();
		this.name = name;
	}

	NamedTerm(String name, Type type) {
		super(type);
		this.name = name;
	}

	//endregion

	//region Public methods

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		NamedTerm namedTerm = (NamedTerm) o;

		return name.equals(namedTerm.name) && getType().equals(namedTerm.getType());

	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}


	//endregion
}
