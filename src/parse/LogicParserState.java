package parse;

import clausal_discovery.core.PredicateDefinition;
import log.Log;
import util.Numbers;
import pair.Pair;
import vector.SafeList;
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
		Term[] terms = new Term[constantNames.length];
		for(int i = 0; i < constantNames.length; i++)
			terms[i] = constants.get(constantNames[i]);
		if(predicates.get(predicateName).isSymmetric())
			Numbers.getPermutations(terms.length).forEach(p -> addInstance(predicateName, p.applyArray(terms)));
		else
			addInstance(predicateName, terms);
	}

	private void addInstance(String predicateName, Term[] terms) {
		Log.LOG.formatLine("INFO Added %s(%s)", predicateName, Arrays.toString(terms));
		instances.add(new PredicateInstance(predicates.get(predicateName).getPredicate(), terms));
	}

	/**
	 * Add an example and reset the current example properties (instances, constants, ...)
	 * @param name	The name of the example
	 */
	public void addExample(String name) {
		Log.LOG.formatLine("INFO added example %s", name);
		SafeList<PredicateInstance> instances1 = SafeList.from(instances.toArray(new PredicateInstance[instances.size()]));
		examples.add(new Example(name, getSetup(), instances1, positiveExample));
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

	public Setup getSetup() {
		Collection<PredicateDefinition> values = this.predicates.values();
		SafeList<PredicateDefinition> definitions = new SafeList<>(values);
		SafeList<Constant> constants = new SafeList<>(this.constants.values());
		return new Setup(new SafeList<>(types.values()), definitions, constants);
	}

	public LogicBase getLogicBase() {
		SafeList<Example> examples = new SafeList<>(this.examples);

		SafeList<PredicateDefinition> search;
		if(searchPredicates.isEmpty())
			search = SafeList.from(predicates.values().toArray(new PredicateDefinition[predicates.values().size()]));
		else
			search = SafeList.from(searchPredicates.toArray(new PredicateDefinition[searchPredicates.size()]));
		return new Knowledge(getSetup().getVocabulary(), examples, search);
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
