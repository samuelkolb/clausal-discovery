package logic.theory;

import clausal_discovery.core.PredicateDefinition;
import clausal_discovery.instance.Instance;
import logic.bias.Type;
import vector.SafeList;

import java.util.*;

/**
 * Collects definitions and their typing information
 *
 * @author Samuel Kolb
 */
public class Vocabulary {

	private final SafeList<Type> types;

	public SafeList<Type> getTypes() {
		return types;
	}
	private final SafeList<PredicateDefinition> definitions;

	public SafeList<PredicateDefinition> getDefinitions() {
		return definitions;
	}

	/**
	 * Creates a new vocabulary with the given definitions
	 * @param definitions	The definitions
	 */
	public Vocabulary(SafeList<Type> types, SafeList<PredicateDefinition> definitions) {
		Set<Type> contained = new HashSet<>();
		for(PredicateDefinition definition : definitions)
			contained.addAll(definition.getTypes());
		this.types = types.filter(type -> !type.isBuiltIn() && contained.contains(type));
		this.definitions = definitions;
	}


	/**
	 * Returns an instance
	 * @param predicateName		The name of the predicate definition
	 * @param variableIndices	The variable indices
	 * @return	An instance
	 */
	public Instance getInstance(String predicateName, SafeList<Integer> variableIndices) {
		return new Instance(getDefinition(predicateName, variableIndices), variableIndices);
	}

	private PredicateDefinition getDefinition(String predicateName, SafeList<Integer> variableIndices) {
		for(PredicateDefinition candidate : getDefinitions())
			if(candidate.getPredicate().getName().equals(predicateName)
					&& candidate.getArity() == variableIndices.length)
				return candidate;
		String message = "No predicate with the name %s and arity %d";
		throw new NoSuchElementException(String.format(message, predicateName,variableIndices.length));
	}
}
