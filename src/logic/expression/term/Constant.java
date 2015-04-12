package logic.expression.term;

import logic.bias.Type;
import logic.expression.visitor.ExpressionVisitor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public class Constant extends NamedTerm {

	//region Variables
	private static final AtomicInteger counter = new AtomicInteger(0);
	//endregion

	//region Construction

	/**
	 * Create a new constant with a unique name
	 */
	public Constant() {
		this("C@" + counter.getAndIncrement());
	}

	/**
	 * Creates a new constant with the given name
	 * @param name	The name of the constant
	 */
	public Constant(String name) {
		super(name);
		counter.incrementAndGet();
	}

	/**
	 * Creates a new constant with the given name and type
	 * @param name	The name of the constant
	 * @param type	the type of the constant
	 */
	public Constant(String name, Type type) {
		super(name, type);
		counter.incrementAndGet();
	}
	//endregion

	//region Public methods

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);
	}

	@Override
	public boolean isGround() {
		return true;
	}

	//endregion
}
