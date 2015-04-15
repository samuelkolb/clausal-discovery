package logic.example;

import association.Association;
import association.HashAssociation;
import association.ListAssociation;
import clausal_discovery.core.PredicateDefinition;
import log.Log;
import logic.bias.Type;
import logic.expression.formula.Predicate;
import logic.expression.formula.PredicateInstance;
import logic.expression.term.Constant;
import logic.expression.visitor.ExpressionLogicPrinter;
import logic.theory.Structure;
import logic.theory.StructureBuilder;
import vector.Vector;

import java.util.Set;

/**
 * Created by samuelkolb on 11/11/14.
 *
 * @author Samuel Kolb
 */
public class Example {

	//region Variables
	private final Setup setup;

	public Setup getSetup() {
		return setup;
	}

	private final Vector<PredicateInstance> instances;

	private final boolean isPositive;

	public boolean isPositive() {
		return isPositive;
	}

	//endregion

	//region Construction

	/**
	 * Creates a new example
	 * @param setup			The example setup
	 * @param instances		The instances present in the example
	 * @param isPositive	Whether or not this is a positive or negative example
	 */
	public Example(Setup setup, Vector<PredicateInstance> instances, boolean isPositive) {
		this.setup = setup;
		this.instances = instances;
		this.isPositive = isPositive;
		if(!isPositive)
			throw new UnsupportedOperationException("Only positive examples are currently supported");
		Set<Predicate> predicates = setup.getPredicates();
		for(PredicateInstance instance : instances)
			if(!predicates.contains(instance.getPredicate()))
				throw new IllegalArgumentException("Instances predicate not in setup");
			else if(!instance.isGround())
				throw new IllegalArgumentException("Examples cannot contain ungrounded elements");
	}

	//endregion

	//region Public methods

	public Structure getStructure() {
		StructureBuilder builder = new StructureBuilder();
		builder.setPositive(isPositive());
		buildTypes(builder);
		buildPredicates(builder);
		return builder.create();
	}

	private void buildTypes(StructureBuilder builder) {
		Association<Type, Constant> typeAssociation = new ListAssociation<>(false, false);
		buildConstants(typeAssociation);
		for(Type type : getSetup().getTypes())
			if(typeAssociation.containsKey(type))
				builder.addConstants(type, typeAssociation.getValues(type));
			else
				builder.addEmptyType(type);
	}

	private void buildPredicates(StructureBuilder builder) {
		Association<Predicate, PredicateInstance> predicateAssociation = new ListAssociation<>(false, false);
		buildPredicates(predicateAssociation);
		for(PredicateDefinition definition : getSetup().getVocabulary().getDefinitions()) {
			Predicate predicate = definition.getPredicate();
			if(!definition.isCalculated()) {
				if(predicateAssociation.containsKey(predicate))
					builder.addPredicateInstances(predicate, predicateAssociation.getValues(predicate));
				else
					builder.addEmptyPredicate(predicate);
			}
		}
	}

	private void buildPredicates(Association<Predicate, PredicateInstance> association) {
		for(PredicateInstance instance : instances)
			association.associate(instance.getPredicate(), instance);
	}

	private void buildConstants(Association<Type, Constant> constants) {
		for(Constant constant : getSetup().getConstants())
			addConstant(constants, constant.getType(), constant);
	}

	private void addConstant(Association<Type, Constant> constants, Type type, Constant constant) {
		constants.associate(type, constant);
		if(type.hasParent() && !type.getParent().isBuiltIn())
			addConstant(constants, type.getParent(), constant);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Example:");
		for(PredicateInstance instance : instances)
			builder.append(" ").append(ExpressionLogicPrinter.print(instance));
		return builder.toString();
	}

	//endregion
}
