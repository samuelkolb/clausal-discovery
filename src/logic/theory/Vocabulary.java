package logic.theory;

import clausal_discovery.core.PredicateDefinition;
import vector.Vector;
import logic.bias.Type;
import logic.expression.formula.Predicate;

import java.util.HashSet;
import java.util.Set;

/**
 * Collects definitions and their typing information
 *
 * @author Samuel Kolb
 */
public class Vocabulary {

	private final Vector<PredicateDefinition> definitions;

	public Vector<PredicateDefinition> getDefinitions() {
		return definitions;
	}

	/**
	 * Creates a new vocabulary with the given definitions
	 * @param definitions	The definitions
	 */
	public Vocabulary(Vector<PredicateDefinition> definitions) {
		this.definitions = definitions;
	}

	public Set<Type> getTypes() {
		Set<Type> types = new HashSet<>();
		for(PredicateDefinition definition : getDefinitions())
			types.addAll(definition.getTypes());
		return types;
	}
}
