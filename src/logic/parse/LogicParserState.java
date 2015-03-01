package logic.parse;

import util.Pair;
import vector.Vector;
import version3.example.clause_discovery.LogicBase;
import logic.bias.Type;
import logic.example.Example;
import logic.example.Setup;
import logic.expression.formula.Predicate;
import logic.expression.formula.PredicateInstance;
import logic.expression.term.Constant;
import logic.expression.term.Term;

import java.util.*;

/**
 * Created by samuelkolb on 24/02/15.
 *
 * @author Samuel Kolb
 */
public class LogicParserState {

	//region Variables
	private Map<String, Type> types = new HashMap<>();
	private Map<String, Predicate> predicates = new HashMap<>();
	private Map<String, Constant> constants = new HashMap<>();
	private Map<String, Example> examples = new HashMap<>();

	private List<PredicateInstance> instances = new ArrayList<>();
	//endregion

	//region Construction

	public LogicParserState() {
		types.put("?", Type.UNDEFINED);
	}

	//endregion

	//region Public methods
	public boolean containsType(String typeName) {
		return types.containsKey(typeName);
	}

	public void addType(String typeName) {
		types.put(typeName, new Type(typeName));
	}

	public boolean containsPredicate(String predicateName) {
		return predicates.containsKey(predicateName);
	}

	public void addPredicate(String predicateName, String[] typeNames) {
		Type[] types = new Type[typeNames.length];
		for(int i = 0; i < typeNames.length; i++)
			types[i] = this.types.get(typeNames[i]);
		predicates.put(predicateName, new Predicate(predicateName, types));
	}

	public boolean containsConstant(String constantName) {
		return constants.containsKey(constantName);
	}

	public void addConstant(String constantName, String typeName) {
		constants.put(constantName, new Constant(constantName, types.get(typeName)));
	}

	public boolean containsExample(String exampleName) {
		return examples.containsKey(exampleName);
	}

	public void addInstance(String predicateName, String[] constantNames) {
		Term[] terms = new Term[constantNames.length];
		for(int i = 0; i < constantNames.length; i++)
			terms[i] = constants.get(constantNames[i]);
		instances.add(new PredicateInstance(predicates.get(predicateName), terms));
	}

	public void addExample(String exampleName) {
		Vector<PredicateInstance> instances1 = new Vector<>(instances.toArray(new PredicateInstance[instances.size()]));
		examples.put(exampleName, new Example(getSetup(), instances1));
		instances.clear();
	}

	public Setup getSetup() {
		Collection<Predicate> values = this.predicates.values();
		Vector<Predicate> predicates = new Vector<>(values.toArray(new Predicate[values.size()]));
		return new Setup(predicates);
	}

	public LogicBase getLogicBase() {
		Collection<Example> values = this.examples.values();
		Vector<Example> examples = new Vector<>(values.toArray(new Example[values.size()]));
		return new Knowledge(getSetup().getVocabulary(), examples);
	}

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
