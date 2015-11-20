package logic.bias;

import logic.expression.formula.Formula;
import logic.expression.term.Constant;
import vector.SafeList;

import java.util.List;

/**
 * Created by samuelkolb on 12/11/15.
 *
 * @author Samuel Kolb
 */
public class EnumType extends Type {

	private final SafeList<Constant> constants;

	public SafeList<Constant> getConstants() {
		return constants;
	}

	/**
	 * Creates a new enum type.
	 * @param name		The name of the type
	 * @param constants	The constants
	 */
	public EnumType(String name, List<String> constants) {
		super(name);
		this.constants = new SafeList<>(constants, s -> new Constant(s, getSubtype(s)));
	}
}
