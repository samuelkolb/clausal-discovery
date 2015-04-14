package clausal_discovery.core;

import clausal_discovery.instance.Instance;
import logic.bias.Type;
import logic.expression.formula.Predicate;
import vector.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * The environment class containsInstance variable indices and their types. It can be used to determine the validity of
 * instances and keep track of typing information.
 *
 * @author Samuel Kolb
 */
public class Environment {

	// IVAR variableTypes - Maps integers to types

	private final Map<Integer, Type> variableTypes;

	/**
	 * Creates a new environment with an empty variable-type map
	 */
	public Environment() {
		this.variableTypes = new HashMap<>();
	}

	private Environment(Map<Integer, Type> variableTypes) {
		this.variableTypes = variableTypes;
	}

	/**
	 * Determines whether an instance is consistent with the typing information stored in this environment
	 * @param instance	The instance to check
	 * @return	True iff the given instance is consistent
	 */
	public boolean isValidInstance(Instance instance) {
		return isValidInstance(instance.getPredicate(), instance.getVariableIndices());
	}

	public boolean isValidInstance(Predicate predicate, Vector<Integer> indices) {
		Map<Integer, Type> variables = new HashMap<>(variableTypes);
		for(int i = 0; i < predicate.getArity(); i++) {
			Integer integer = indices.get(i);
			Type type = predicate.getTypes().get(i);
			if(!variables.containsKey(integer) || variables.get(integer).isSuperTypeOf(type))
				variables.put(integer, type);
			else if(!type.isSuperTypeOf(variables.get(integer)))
				return false;
		}
		return true;
	}

	/**
	 * Adds the given instance by processing its typing information
	 * @param instance	The instance to add
	 * @return	A new environment that containsInstance additional typing information contained in the given instance
	 */
	public Environment addInstance(Instance instance) {
		return addInstance(instance.getPredicate(), instance.getVariableIndices());
	}

	protected Environment addInstance(Predicate predicate, Vector<Integer> indices) {
		Map<Integer, Type> variables = new HashMap<>(variableTypes);
		for(int i = 0; i < predicate.getArity(); i++) {
			Integer integer = indices.get(i);
			Type type = predicate.getTypes().get(i);
			if (!variables.containsKey(integer) || variables.get(integer).isSuperTypeOf(type))
				variables.put(integer, type);
			else if (!type.isSuperTypeOf(variables.get(integer)))
				throw new IllegalStateException();
		}
		return new Environment(variables);
	}
}
