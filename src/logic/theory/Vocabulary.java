package logic.theory;

import vector.Vector;
import logic.bias.Type;
import logic.expression.formula.Predicate;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by samuelkolb on 30/10/14.
 */
public class Vocabulary {

	private final Vector<Predicate> predicates;

	public Vector<Predicate> getPredicates() {
		return predicates;
	}

	public Vocabulary(Predicate... predicates) {
		this(new Vector<Predicate>(predicates));
	}

	public Vocabulary(Vector<Predicate> predicates) {
		this.predicates = predicates;
	}

	public Set<Type> getTypes() {
		Set<Type> types = new HashSet<>();
		for(Predicate predicate : getPredicates())
			types.addAll(predicate.getTypes());
		return types;
	}
}
