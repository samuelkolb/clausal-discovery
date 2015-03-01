package logic.example;

import vector.Vector;
import logic.bias.Type;
import logic.expression.formula.Predicate;
import logic.theory.Vocabulary;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by samuelkolb on 11/11/14.
 *
 * @author Samuel Kolb
 */
public class Setup {

	//region Variables
	private final Vocabulary vocabulary;

	public Vocabulary getVocabulary() {
		return vocabulary;
	}

	//endregion

	//region Construction

	public Setup(Vector<Predicate> predicates) {
		this.vocabulary = new Vocabulary(predicates);
	}

	//endregion

	//region Public methods
	public Set<Predicate> getPredicates() {
		return new HashSet<>(getVocabulary().getPredicates());
	}

	public Set<Type> getTypes() {
		return getVocabulary().getTypes();
	}

	//endregion
}
