package logic.theory;

import basic.StringUtil;
import vector.SafeList;
import logic.bias.Type;
import logic.expression.term.Constant;

/**
 * Created by samuelkolb on 11/11/14.
 *
 * @author Samuel Kolb
 */
class ConstantTypeElement extends Structure.TypeElement {

	private final SafeList<Constant> constants;

	public ConstantTypeElement(Type type) {
		this(type, new SafeList<>());
	}

	public ConstantTypeElement(Type type, SafeList<Constant> constants) {
		super(type);
		this.constants = constants;
		for(Constant constant : constants)
			if(!getType().isSuperTypeOf(constant.getType()))
				throw new IllegalArgumentException("Incorrect constant type");
	}

	public ConstantTypeElement(Constant... constants) {
		super(constants[0].getType());
		for(int i = 1; i < constants.length; i++)
			if(!getType().equals(constants[i].getType()))
				throw new IllegalArgumentException("Inconsistent constant types");
		this.constants = new SafeList<>(constants);
	}

	@Override
	public String print() {
		return getType().getName() + " = {" + StringUtil.join("; ", constants.getArray()) + "}";
	}
}


