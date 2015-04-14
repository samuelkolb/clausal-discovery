package logic.parse;

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

	private final Map<String, Predicate> predicates = new HashMap<>();

	private final Map<String, Example> examples = new HashMap<>();

	private final List<PredicateInstance> instances = new ArrayList<>();

	private boolean positiveExample = true;

	private final Map<String, Constant> constants = new HashMap<>();

	private final List<Predicate> searchPredicates = new ArrayList<>();
	//endregion

	//region Construction

	/**
	 * Creates a new logic parser state containing only the unknown type and the integer type
	 */
	public LogicParserState() {
		types.put("?", Type.UNDEFINED);
		types.put("int", new Type("int"));
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
	 * @param typeNames		The names of the predicate arguments types
	 */
	public void addPredicate(String predicateName, boolean symmetric, String[] typeNames) {
		Log.LOG.printLine("INFO added predicate " + predicateName + Arrays.toString(typeNames));
		Type[] types = new Type[typeNames.length];
		for(int i = 0; i < typeNames.length; i++)
			types[i] = this.types.get(typeNames[i]);
		Predicate predicate = new Predicate(predicateName, symmetric, types);
		predicates.put(predicateName, predicate);
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
	 * Checks whether this state containsInstance an example with the given name
	 * @param exampleName	The name of the example to check for
	 * @return	True iff an example with the given name has already been added to this state
	 */
	public boolean containsExample(String exampleName) {
		return examples.containsKey(exampleName);
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
		instances.add(new PredicateInstance(predicates.get(predicateName), terms));
	}

	/**
	 * Add an example and reset the current example properties (instances, constants, ...)
	 * @param exampleName	The name of the example
	 */
	public void addExample(String exampleName) {
		Log.LOG.printLine("INFO added example " + exampleName);
		Vector<PredicateInstance> instances1 = new Vector<>(instances.toArray(new PredicateInstance[instances.size()]));
		examples.put(exampleName, new Example(getSetup(), instances1, positiveExample));
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
		Collection<Predicate> values = this.predicates.values();
		Vector<Predicate> predicates = new Vector<>(values.toArray(new Predicate[values.size()]));
		return new Setup(predicates);
	}

	public LogicBase getLogicBase() {
		Collection<Example> values = this.examples.values();
		Vector<Example> examples = new Vector<>(values.toArray(new Example[values.size()]));

		Vector<Predicate> search;
		if(searchPredicates.isEmpty())
			search = new Vector<>(predicates.values().toArray(new Predicate[predicates.values().size()]));
		else
			search = new Vector<>(searchPredicates.toArray(new Predicate[searchPredicates.size()]));
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
