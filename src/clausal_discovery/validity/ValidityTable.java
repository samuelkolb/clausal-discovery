package clausal_discovery.validity;

import clausal_discovery.configuration.Configuration;
import clausal_discovery.core.LogicBase;
import clausal_discovery.core.StatusClause;
import idp.IdpExecutor;
import logic.example.Example;
import logic.expression.formula.Formula;
import logic.theory.InlineTheory;
import logic.theory.KnowledgeBase;
import logic.theory.Structure;
import logic.theory.Theory;
import vector.Vector;
import vector.WriteOnceVector;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores validity values for pairs of clauses and examples
 *
 * @author Samuel Kolb
 */
public class ValidityTable {

	//region Variables

	private final int clauseCount;

	public int getClauseCount() {
		return clauseCount;
	}

	private final Map<Example, Vector<Boolean>> validity;

	//endregion

	//region Construction

	private ValidityTable(int clauseCount, Map<Example, Vector<Boolean>> validity) {
		this.clauseCount = clauseCount;
		this.validity = validity;
	}

	/**
	 * Returns a new validity table without the specified clause
	 * @param index	The index of the clause to remove
	 * @return	The new validity table
	 */
	public ValidityTable removeClause(int index) {
		Map<Example, Vector<Boolean>> map = new HashMap<>();
		for(Example example : validity.keySet())
			map.put(example, validity.get(example).leaveOut(index));
		return new ValidityTable(getClauseCount() - 1, map);
	}

	/**
	 * Creates a new validity table using the given validated clauses
	 * @param clauses	The clauses with their validity values
	 * @return	A new validity table
	 */
	public static ValidityTable create(Vector<ValidatedClause> clauses) {
		return new ValidityTable(clauses.size(), createValidityMap(clauses));
	}

	private static Map<Example, Vector<Boolean>> createValidityMap(Vector<ValidatedClause> clauses) {
		Map<Example, Vector<Boolean>> validity = new LinkedHashMap<>();
		if(clauses.isEmpty())
			return validity;
		for(int i = 0; i < clauses.get(0).getLogicBase().getExamples().size(); i++) {
			Vector<Boolean> vector = new WriteOnceVector<>(new Boolean[clauses.length]);
			for(ValidatedClause clause : clauses)
				vector.add(clause.getValidity().get(i));
			validity.put(clauses.get(0).getLogicBase().getExamples().get(i), vector);
		}
		return validity;
	}

	/**
	 * Extracts logic base and background from configuration
	 * @param configuration	The configuration
	 * @param clauses		The clauses to check validity for
	 * @return	The validity table
	 */
	public static ValidityTable create(Configuration configuration, Vector<StatusClause> clauses) {
		return create(configuration.getLogicBase(), configuration.getBackgroundTheories(), clauses);
	}

	/**
	 * Creates a new validity table for the given examples by calculating the necessary validity values
	 * @param logicBase		The logic base containing the examples to check
	 * @param background	The background theories
	 * @param clauses		The clauses to check validity for
	 * @return	The validity table
	 */
	public static ValidityTable create(LogicBase logicBase, Vector<Theory> background, Vector<StatusClause> clauses) {
		ValidityCalculator calculator = new ParallelValidityCalculator(logicBase, IdpExecutor.get(), background);
		ValidityTable table = create(clauses.map(ValidatedClause.class, calculator::getValidatedClause));
		calculator.shutdown();
		return table;
	}

	/**
	 * Creates a new validity table for the examples in the given logic base. The validity values are calculated for the
	 * given formulas.
	 * @param logicBase	The logic base containing the vocabulary and the examples
	 * @param clauses	The clauses to test validity for
	 * @return	The validity table
	 */
	public static ValidityTable create(LogicBase logicBase, List<Formula> clauses) {
		Vector<Theory> theories = new Vector<>(Formula.class, clauses).map(Theory.class, InlineTheory::new);
		Vector<Structure> structures = logicBase.getExamples().map(Structure.class, Example::getStructure);
		KnowledgeBase base = new KnowledgeBase(logicBase.getVocabulary(), theories, structures);
		List<Vector<Boolean>> validity = IdpExecutor.get().testValidityTheories(base);
		Map<Example, Vector<Boolean>> map = new HashMap<>();
		for(int i = 0; i < logicBase.getExamples().size(); i++) {
			boolean[] array = new boolean[validity.size()];
			for(int j = 0; j < array.length; j++)
				array[j] = validity.get(j).get(i);
			map.put(logicBase.getExamples().get(i), Vector.create(array));
		}
		return new ValidityTable(clauses.size(), map);
	}

	/*private static Map<Example, Vector<Boolean>> createValidityMap(LogicBase logicBase, Vector<Theory> background,
															Vector<StatusClause> clauses) {

		Map<Example, Vector<Boolean>> validity = new LinkedHashMap<>();
		ValidityCalculator[] calculators = new ValidityCalculator[logicBase.getExamples().size()];
		List<LogicBase> logicBases = logicBase.split();
		Vector<Formula> formulas = clauses.map(Formula.class, new StatusClauseConverter());
		for(int i = 0; i < logicBase.getExamples().size(); i++) {
			calculators[i] = new ParallelValidityCalculator(logicBases.get(i), IdpExecutor.get(), background);
			for(StatusClause clause : clauses)
				calculators[i].submitFormula(new StatusClauseConverter().apply(clause));
		}
		for(int i = 0; i < logicBase.getExamples().size(); i++) {
			Boolean[] booleans = new Boolean[formulas.size()];
			for(int j = 0; j < formulas.size(); j++)
				booleans[j] = calculators[i].isValid(formulas.get(j));
			calculators[i].shutdown();
			validity.put(logicBase.getExamples().get(i), new Vector<>(booleans));
		}
		return validity;
	}*/

	//endregion

	//region Public methods

	public Vector<Example> getExamples() {
		return new Vector<>(Example.class, this.validity.keySet());
	}

	/**
	 * Returns the validity vector for the given example
	 * @param example	The example to return the values for
	 * @return	A vector of booleans, where return.get(i) == true indicates that the example is valid for the ith clause
	 */
	public Vector<Boolean> getValidity(Example example) {
		if(!this.validity.containsKey(example))
			throw new IllegalArgumentException("Example not in validity table: " + example);
		return this.validity.get(example);
	}

	//endregion
}
