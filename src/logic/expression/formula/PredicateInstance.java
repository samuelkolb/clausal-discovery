package logic.expression.formula;

import vector.Vector;
import logic.expression.term.Term;
import logic.expression.visitor.ExpressionVisitor;

/**
 * Created by samuelkolb on 22/10/14.
 *
 * @author Samuel Kolb
 */
public class PredicateInstance extends Atom {

	//region Variables
	private static final String arityError = "%d terms provided to predicate %s of arity %d";

	private final Predicate predicate;

	public Predicate getPredicate() {
		return predicate;
	}

	private final Vector<Term> terms;

	public Vector<Term> getTerms() {
		return terms;
	}

	public Term getTerm(int index) {
		return terms.get(index);
	}

	//endregion

	//region Construction

	public PredicateInstance(Predicate predicate) {
		this(predicate, new Term[0]);
	}

	public PredicateInstance(Predicate predicate, Term... terms) {
		this.predicate = predicate;
		this.terms = new Vector<>(terms);
		int arity = getPredicate().getArity();
		if(getTerms().length != arity)
			throw new IllegalArgumentException(String.format(arityError, getTerms().length, predicate.getName(), arity));
		for(int i = 0; i < getPredicate().getArity(); i++)
			getTerm(i).setType(getPredicate().getTypes().e(i));
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
