package clausal_discovery.core;

import clausal_discovery.validity.ParallelValidityCalculator;
import clausal_discovery.validity.ValidityCalculator;
import idp.IdpExecutor;
import logic.example.Example;
import logic.expression.formula.Formula;
import logic.theory.Theory;
import vector.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by samuelkolb on 28/04/15.
 *
 * @author Samuel Kolb
 */
public class ValidityTable {

	//region Variables

	private final Vector<Example> examples;

	public Vector<Example> getExamples() {
		return examples;
	}

	private final Vector<StatusClause> clauses;

	public Vector<StatusClause> getClauses() {
		return clauses;
	}

	private final Map<Example, Vector<Boolean>> validity = new HashMap<>();

	//endregion

	//region Construction

	/**
	 * Creates a new validity table for the given examples
	 * @param logicBase		The logic base containing the examples to check
	 * @param background	The background theories
	 * @param clauses		The clauses to check validity for
	 */
	public ValidityTable(LogicBase logicBase, Vector<Theory> background, Vector<StatusClause> clauses) {
		this.examples = logicBase.getExamples();
		this.clauses = clauses;
		createValidityMap(logicBase, background);
	}

	private void createValidityMap(LogicBase logicBase, Vector<Theory> background) {
		ValidityCalculator[] calculators = new ValidityCalculator[logicBase.getExamples().size()];
		List<LogicBase> logicBases = logicBase.split();
		Vector<Formula> formulas = getClauses().map(Formula.class, new StatusClauseConverter());
		for(int i = 0; i < logicBase.getExamples().size(); i++) {
			calculators[i] = new ParallelValidityCalculator(logicBases.get(i), IdpExecutor.get(), background);
			for(StatusClause clause : getClauses())
				calculators[i].submitFormula(new StatusClauseConverter().apply(clause));
		}
		for(int i = 0; i < logicBase.getExamples().size(); i++) {
			Boolean[] booleans = new Boolean[formulas.size()];
			for(int j = 0; j < formulas.size(); j++)
				booleans[j] = calculators[i].isValid(formulas.get(j));
			calculators[i].shutdown();
			this.validity.put(logicBase.getExamples().get(i), new Vector<>(booleans));
		}
	}

	//endregion

	//region Public methods

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
