package logic.expression.formula;

import logic.bias.Type;
import vector.SafeList;
import logic.expression.term.Term;
import logic.expression.visitor.ExpressionVisitor;

import java.util.List;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public class PredicateInstance extends Atom {

	//region Variables
	private final Predicate predicate;

	public Predicate getPredicate() {
		return predicate;
	}

	private final SafeList<Term> terms;

	public SafeList<Term> getTerms() {
		return terms;
	}

	public Term getTerm(int index) {
		return terms.get(index);
	}

	//endregion

	//region Construction

	/**
	 * Creates a predicate instance with no terms.
	 * @param predicate	The predicate definition of arity 0
	 */
	public PredicateInstance(Predicate predicate) {
		this(predicate, new SafeList<>());
	}

	/**
	 * Creates a predicate instance.
	 * @param predicate	The predicate definition
	 * @param terms		The terms
	 */
	public PredicateInstance(Predicate predicate, Term... terms) {
		this(predicate, SafeList.from(terms));
	}

	/**
	 * Creates a predicate instance.
	 * @param predicate	The predicate definition
	 * @param terms		The terms
	 */
	public PredicateInstance(Predicate predicate, List<Term> terms) {
		test(predicate, terms);
		this.predicate = predicate;
		this.terms = SafeList.from(terms);
	}

	protected void test(Predicate predicate, List<Term> terms) {
		int arity = predicate.getArity();
		if(arity != terms.size()) {
			final String message = "%d terms provided to predicate %s of arity %d";
			throw new IllegalArgumentException(String.format(message, getTerms().size(), predicate.getName(), arity));
		}
		for(int i = 0; i < arity; i++) {
			Type type = predicate.getTypes().get(i);
			if(!type.isSuperTypeOf(terms.get(i).getType())) {
				final String message = "Term %s should be of type %s but was of type %s";
				throw new IllegalArgumentException(String.format(message, terms.get(i), type, terms.get(i).getType()));
			}
		}
	}

	//endregion

	//region Public methods

	@Override
	public boolean isGround() {
		for(Term term : getTerms())
			if(!term.isGround())
				return false;
		return true;
	}

	@Override
	public boolean isTrue() {
		return false;
	}

	@Override
	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);
	}

	/**
	 * Creates an instance of this predicate
	 * @param terms	The terms to create the instance with
	 * @return	A predicate instance with the given terms
	 */
	public PredicateInstance instance(Term[] terms) {
		return new PredicateInstance(this.predicate, terms);
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		PredicateInstance instance = (PredicateInstance) o;

		return predicate.equals(instance.predicate) && terms.equals(instance.terms);

	}

	@Override
	public int hashCode() {
		int result = predicate.hashCode();
		result = 31 * result + terms.hashCode();
		return result;
	}

	//endregion
}
