package logic.expression.formula;

import log.LinkTransformer;
import log.Log;
import logic.expression.Expression;
import logic.expression.term.Constant;
import logic.expression.term.Variable;
import logic.expression.visitor.ExpressionLogicPrinter;
import logic.expression.visitor.ExpressionSkolemizer;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public abstract class Formula implements Expression {

	//region Variables

	//endregion

	//region Construction
	Formula() {

	}
	//endregion

	//region Public methods
	public abstract boolean isTrue();

	public static void main(String[] args) {
		Log.LOG.addTransformer(new LinkTransformer());
		Predicate good_book = new Predicate("good_book", 0);
		Predicate has_book = new Predicate("has_book", 2);
		Predicate good = new Predicate("good", 1);
		Constant course = new Constant("C");
		Variable book = new Variable("b");
		Formula formula = Clause.horn(
				new PredicateInstance(good_book),
				new PredicateInstance(has_book, course, book),
				new PredicateInstance(good, book)
		);
		Log.LOG.printLine(ExpressionLogicPrinter.print(formula));
		Expression skolemized = new ExpressionSkolemizer().skolemize(formula);
		Log.LOG.printLine(ExpressionLogicPrinter.print(skolemized));
	}
	//endregion
}
