package clausal_discovery.validity;

import clausal_discovery.core.LogicBase;
import clausal_discovery.core.StatusClause;
import clausal_discovery.core.StatusClauseConverter;
import idp.IdpExecutor;
import logic.example.Example;
import logic.expression.formula.Formula;
import logic.theory.Theory;
import vector.Vector;
import vector.WriteOnceVector;

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
		return this.validity.get(example);
	}

	//endregion
}
