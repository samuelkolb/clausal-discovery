package logic.example;

import clausal_discovery.core.PredicateDefinition;
import logic.expression.term.Constant;
import vector.SafeList;
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

	// IVAR vocabulary - The vocabulary of this setup

	private final Vocabulary vocabulary;

	public Vocabulary getVocabulary() {
		return vocabulary;
	}

	// IVAR constants - The constants defined in this setup

	private final SafeList<Constant> constants;

	public SafeList<Constant> getConstants() {
		return constants;
	}

	//endregion

	//region Construction

	/**
	 * Creates a new setup
	 * @param definitions	The predicate definitions to be used
	 * @param constants		The constants to be used
	 */
	public Setup(SafeList<Type> types, SafeList<PredicateDefinition> definitions, SafeList<Constant> constants) {
		this.vocabulary = new Vocabulary(types, definitions);
		this.constants = constants;
	}

	//endregion

	//region Public methods
	public Set<Predicate> getPredicates() {
		HashSet<Predicate> predicates = new HashSet<>();
		for(PredicateDefinition definition : getVocabulary().getDefinitions())
			predicates.add(definition.getPredicate());
		return predicates;
	}

	public SafeList<Type> getTypes() {
		return getVocabulary().getTypes();
	}

	//endregion
}
