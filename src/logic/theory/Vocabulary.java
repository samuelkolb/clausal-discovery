package logic.theory;

import clausal_discovery.core.PredicateDefinition;
import clausal_discovery.instance.Instance;
import logic.bias.Type;
import vector.Vector;

import java.util.HashSet;
import java.util.NoSuchElementException;
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

	/**
	 * Returns an instance
	 * @param predicateName		The name of the predicate definition
	 * @param variableIndices	The variable indices
	 * @return	An instance
	 */
	public Instance getInstance(String predicateName, Vector<Integer> variableIndices) {
		return new Instance(getDefinition(predicateName, variableIndices), variableIndices);
	}

	private PredicateDefinition getDefinition(String predicateName, Vector<Integer> variableIndices) {
		for(PredicateDefinition candidate : getDefinitions())
			if(candidate.getPredicate().getName().equals(predicateName)
					&& candidate.getArity() == variableIndices.length)
				return candidate;
		String message = "No predicate with the name %s and arity %d";
		throw new NoSuchElementException(String.format(message, predicateName,variableIndices.length));
	}
}
