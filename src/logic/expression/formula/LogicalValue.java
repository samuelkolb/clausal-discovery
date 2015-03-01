package logic.expression.formula;

import logic.expression.visitor.ExpressionVisitor;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public class LogicalValue extends Atom {

	//region Variables
	public static final LogicalValue TRUE = new LogicalValue(true);

	public static final LogicalValue FALSE = new LogicalValue(false);

	private final boolean truthValue;
	//endregion

	//region Construction

	private LogicalValue(boolean truthValue) {
		this.truthValue = truthValue;
	}

	//endregion

	//region Public methods

	@Override
	public boolean isGround() {
		return true;
	}

	@Override
	public boolean isTrue() {
		return truthValue;
	}

	@Override
	public String toString() {
		return isTrue() ? "true" : "false";
	}

	@Override
	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		LogicalValue that = (LogicalValue) o;

		return truthValue == that.truthValue;

	}

	@Override
	public int hashCode() {
		return (truthValue ? 1 : 0);
	}

	//endregion
}
