package logic.theory;

import basic.StringUtil;
import vector.Vector;
import logic.bias.Type;
import logic.expression.term.Constant;

/**
 * Created by samuelkolb on 11/11/14.
 *
 * @author Samuel Kolb
 */
class ConstantTypeElement extends Structure.TypeElement {

	private final Vector<Constant> constants;

	public ConstantTypeElement(Type type) {
		this(type, new Vector<>());
	}

	public ConstantTypeElement(Type type, Vector<Constant> constants) {
		super(type);
		this.constants = constants;
		for(Constant constant : constants)
			if(!getType().equals(constant.getType()))
				throw new IllegalArgumentException("Incorrect constant type");
	}

	public ConstantTypeElement(Constant... constants) {
		super(constants[0].getType());
		for(int i = 1; i < constants.length; i++)
			if(!getType().equals(constants[i].getType()))
				throw new IllegalArgumentException("Inconsistent constant types");
		this.constants = new Vector<>(constants);
	}

	@Override
	public String print() {
		return getType().getName() + " = {" + StringUtil.join("; ", constants.getArray()) + "}";
	}
}


