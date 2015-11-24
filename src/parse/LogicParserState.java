package parse;

import clausal_discovery.core.LogicBase;
import clausal_discovery.core.PredicateDefinition;
import clausal_discovery.core.bias.SymmetricPredicateDefinition;
import log.Log;
import logic.bias.EnumType;
import logic.bias.Type;
import logic.example.Example;
import logic.example.Setup;
import logic.expression.formula.Predicate;
import logic.expression.formula.PredicateInstance;
import logic.expression.term.Constant;
import logic.expression.term.Term;
import logic.theory.Vocabulary;
import pair.Pair;
import vector.SafeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The logic parser state stores state information that is added during the parsing of a logic file
 *
 * @author Samuel Kolb
 */
public class LogicParserState {

	//region Variables
	private final Map<String, Type> types = new LinkedHashMap<>();

	private final Map<String, PredicateDefinition> predicates = new LinkedHashMap<>();

	private final List<Example> examples = new ArrayList<>();

	private final List<PredicateInstance> instances = new ArrayList<>();

	private boolean positiveExample = true;

	private final Map<String, Constant> constants = new HashMap<>();

	private final List<PredicateDefinition> searchPredicates = new ArrayList<>();

	//endregion

	//region Construction

	/**
	 * Creates a new logic parser state containing only the unknown type and the integer type
	 */
	public LogicParserState() {
		types.put("?", Type.GENERIC);
		types.put("int", Type.createBuiltIn("int"));
		types.put("nat", Type.createBuiltIn("nat"));
	}

	//endregion

	//region Public methods

	/**
	 * Checks whether this state containsInstance a type with the given name
	 * @param typeName	The name of the type to check for
	 * @return	True iff a type with the given name has already been added to this state
	 */
	public boolean containsType(String typeName) {
		return types.containsKey(typeName);
	}

	/**
	 * Adds a type with the given name
	 * @param typeName	The name of the type
	 */
	public void addType(String typeName) {
		Log.LOG.printLine("INFO added type " + typeName);
		types.put(typeName, new Type(typeName));
	}

	/**
	 * Adds a subtype with the given name
	 * @param superTypeName	The name of the super type
	 * @param subTypeName 	The name of the sub type
	 */
	public void addSubType(String superTypeName, String subTypeName) {
		Log.LOG.printLine("INFO added type " + subTypeName + " > " + superTypeName);
		types.put(subTypeName, types.get(superTypeName).getSubtype(subTypeName));
	}

	/**
	 * Adds the given enum type.
	 * @param enumType	The enum to add
	 */
	public void addEnum(EnumType enumType) {
		Log.LOG.printLine("INFO added enum " + enumType.getName());
		types.put(enumType.getName(), enumType);
		enumType.getConstants().forEach(c -> types.put(c.getType().getName(), c.getType()));
	}

	/**
	 * Checks whether this state containsInstance a predicate definition with the given name
	 * @param predicateName	The name of the predicate to check for
	 * @return	True iff a predicate definition with the given name has already been added to this state
	 */
	public boolean containsPredicate(String predicateName) {
		return predicates.containsKey(predicateName);
	}

	/**
	 * Adds a predicate definition with the given name and type names
	 * @param predicateName	The name of the predicate
	 * @param symmetric		Whether or not this is a symmetric predicate
	 * @param calculated	Whether or not this is a calculated predicate
	 * @param typeNames		The names of the predicate arguments types
	 */
	public void addPredicate(String predicateName, boolean symmetric, boolean calculated, String[] typeNames) {
		Log.LOG.printLine("INFO added predicate " + predicateName + Arrays.toString(typeNames));
		SafeList<Type> types = SafeList.from(typeNames).map(this.types::get);
		Predicate predicate = new Predicate(predicateName, types);
		PredicateDefinition definition = symmetric
				? new SymmetricPredicateDefinition(predicate)
				: new PredicateDefinition(predicate, calculated);
		predicates.put(predicateName, definition);
	}

	/**
	 * Checks whether this state containsInstance a constant with the given name
	 * @param constantName	The name of the constant to check for
	 * @return	True iff a constant with the given name has already been added to this state
	 */
	public boolean containsConstant(String constantName) {
		return constants.containsKey(constantName);
	}

	/**
	 * Adds a constant to the state
	 * @param constantName	The name of the constant
	 * @param typeName		The name of the constants type
	 */
	public void addConstant(String constantName, String typeName) {
		Log.LOG.printLine("INFO added constant " + typeName + " " + constantName);
		constants.put(constantName, new Constant(constantName, types.get(typeName)));
	}

	/**
	 * Add a predicate instance with the given name and constant names
	 * @param predicateName	The name of the predicate
	 * @param constantNames	The names of the constant arguments of the predicate
	 */
	public void addInstance(String predicateName, String[] constantNames) {
		Term[] terms = new Term[constantNames.length];
		for(int i = 0; i < constantNames.length; i++)
			terms[i] = constants.get(constantNames[i]);
		predicates.get(predicateName).getGroundInstances(terms).forEach(this::addInstance);
	}

	private void addInstance(PredicateInstance instance) {
		Log.LOG.formatLine("INFO Added %s(%s)", instance.getPredicate().getName(), instance.getTerms());
		instances.add(instance);
	}

	/**
	 * Resets the example state.
	 */
	public void resetExample() {
		instances.clear();
		constants.clear();
		types.values().stream().filter(type -> type instanceof EnumType)
				.forEach(type -> ((EnumType) type).getConstants().forEach(c -> constants.put(c.getName(), c)));
		positiveExample = true;
	}

	/**
	 * Add an example and reset the current example properties (instances, constants, ...)
	 * @param name	The name of the example
	 */
	public void addExample(String name) {
		Log.LOG.formatLine("INFO added example %s", name);
		SafeList<PredicateInstance> instances = SafeList.from(this.instances);
		examples.add(new Example(name, getSetup(), instances, positiveExample));
	}

	public void setPositiveExample(boolean b) {
		Log.LOG.printLine("INFO set " + (b ? "positive" : "negative") + " example");
		positiveExample = b;
	}

	/**
	 * Adds the predicate with the given name to the list of search predicates
	 * @param predicateName	The name of the predicate
	 */
	public void addSearchPredicate(String predicateName) {
		Log.LOG.printLine("INFO added search predicate " + predicateName);
		searchPredicates.add(predicates.get(predicateName));
	}

	public Setup getSetup() {
		SafeList<PredicateDefinition> definitions = SafeList.from(this.predicates.values());
		SafeList<Constant> constants = SafeList.from(this.constants.values());
		SafeList<Type> types = SafeList.from(this.types.values());
		return new Setup(types, definitions, constants);
	}

	public LogicBase getLogicBase() {
		boolean customSearch = !searchPredicates.isEmpty();
		SafeList<PredicateDefinition> search = SafeList.from(customSearch ? searchPredicates : predicates.values());
		SafeList<EnumType> enumList = SafeList.from(this.types.values()).filter(EnumType.class);
		List<PredicateDefinition> enumDefinitions = new ArrayList<>();
		enumList.forEach(t -> t.getConstants().forEach(c -> enumDefinitions.add(t.getPredicateDefinition(c))));
		return new Knowledge(getSetup().getVocabulary(), this.examples, search.grow(enumDefinitions));
	}

	/**
	 * Parses a predicate string into the name and an array of argument names
	 * @param string	The string representing a predicate
	 * @return	A pair containing the name of the predicate and an array of argument names
	 * @throws IllegalArgumentException	Iff the string is badly formatted
	 */
	public Pair<String, String[]> parsePredicate(String string) throws IllegalArgumentException {
		string = string.trim();
		String[] predicateParts = string.split("\\(");
		if(predicateParts.length != 2 || !string.endsWith(")"))
			throw new IllegalArgumentException();
		String name = predicateParts[0].trim();
		String[] args = predicateParts[1].substring(0, predicateParts[1].length() - 1).split(",");
		for(int i = 0; i < args.length; i++)
			args[i] = args[i].trim();
		return new Pair.Implementation<>(name, args);
	}

	//endregion
}
