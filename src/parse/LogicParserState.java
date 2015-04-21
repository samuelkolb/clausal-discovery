package parse;

import clausal_discovery.core.PredicateDefinition;
import log.Log;
import util.Pair;
import vector.Vector;
import clausal_discovery.core.LogicBase;
import logic.bias.Type;
import logic.example.Example;
import logic.example.Setup;
import logic.expression.formula.Predicate;
import logic.expression.formula.PredicateInstance;
import logic.expression.term.Constant;
import logic.expression.term.Term;

import java.util.*;

/**
 * The logic parser state stores state information that is added during the parsing of a logic file
 *
 * @author Samuel Kolb
 */
public class LogicParserState {

	//region Variables
	private final Map<String, Type> types = new HashMap<>();

	private final Map<String, PredicateDefinition> predicates = new LinkedHashMap<>();

	private final List<Example> examples = new ArrayList<>();

	private final List<PredicateInstance> instances = new ArrayList<>();

	private boolean positiveExample = true;

	private final Map<String, Constant> constants = new HashMap<>();

	private final List<PredicateDefinition> searchPredicates = new ArrayList<>();

	private final List<Vector<Integer>> preferences = new ArrayList<>();
	//endregion

	//region Construction

	/**
	 * Creates a new logic parser state containing only the unknown type and the integer type
	 */
	public LogicParserState() {
		types.put("?", Type.GENERIC);
		types.put("int", Type.createBuiltIn("int"));
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
		Type[] types = new Type[typeNames.length];
		for(int i = 0; i < typeNames.length; i++)
			types[i] = this.types.get(typeNames[i]);
		Predicate predicate = new Predicate(predicateName, types);
		predicates.put(predicateName, new PredicateDefinition(predicate, symmetric, calculated));
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
		Log.LOG.printLine("INFO added instance " + predicateName + Arrays.toString(constantNames));
		Term[] terms = new Term[constantNames.length];
		for(int i = 0; i < constantNames.length; i++)
			terms[i] = constants.get(constantNames[i]);
		instances.add(new PredicateInstance(predicates.get(predicateName).getPredicate(), terms));
	}

	/**
	 * Add an example and reset the current example properties (instances, constants, ...)
	 */
	public void addExample() {
		Log.LOG.printLine("INFO added example");
		Vector<PredicateInstance> instances1 = new Vector<>(instances.toArray(new PredicateInstance[instances.size()]));
		examples.add(new Example(getSetup(), instances1, positiveExample));
		instances.clear();
		constants.clear();
		positiveExample = true;
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

	/**
	 * Add a preference
	 * @param preference	The list of examples in their order of preference (examples[0] > ... > examples[n])
	 */
	public void addPreference(Vector<Integer> preference) {
		this.preferences.add(preference);
	}

	public Setup getSetup() {
		Collection<PredicateDefinition> values = this.predicates.values();
		Vector<PredicateDefinition> definitions = new Vector<>(PredicateDefinition.class, values);
		Vector<Constant> constants = new Vector<>(Constant.class, this.constants.values());
		return new Setup(definitions, constants);
	}

	public LogicBase getLogicBase() {
		Vector<Example> examples = new Vector<>(this.examples.toArray(new Example[this.examples.size()]));

		Vector<PredicateDefinition> search;
		if(searchPredicates.isEmpty())
			search = new Vector<>(predicates.values().toArray(new PredicateDefinition[predicates.values().size()]));
		else
			search = new Vector<>(searchPredicates.toArray(new PredicateDefinition[searchPredicates.size()]));
		return new Knowledge(getSetup().getVocabulary(), examples, search);
	}

	public List<Vector<Integer>> getPreferences() {
		return new ArrayList<>(this.preferences);
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
