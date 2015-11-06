package clausal_discovery.validity;

import cern.colt.bitvector.BitMatrix;
import cern.colt.bitvector.BitVector;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores validity values for pairs of clauses and examples
 *
 * @author Samuel Kolb
 */
public class ValidityTable {

	//region Variables

	public final Map<Example, Integer> examples = new HashMap<>();

	public int getClauseCount() {
		return validity.rows();
	}

	/**
	 * One row per clause
	 * One column per example
	 */
	private final BitMatrix validity;

	//endregion

	//region Construction

	private ValidityTable(BitMatrix validity, List<Example> examples) {
		this.validity = validity;
		for(int i = 0; i < examples.size(); i++)
			this.examples.put(examples.get(i), i);
	}

	/**
	 * Creates a new validity table using the given validated clauses
	 * @param clauses	The clauses with their validity values
	 * @return	A new validity table
	 */
	public static ValidityTable create(Vector<ValidatedClause> clauses) {
		Vector<Example> examples = clauses.isEmpty() ? new Vector<>() : clauses.getFirst().getLogicBase().getExamples();
		return new ValidityTable(createValidityMap(clauses), examples);
	}

	private static BitMatrix createValidityMap(Vector<ValidatedClause> clauses) {
		if(clauses.isEmpty())
			return new BitMatrix(0, 0);
		int numberExamples = clauses.getFirst().getLogicBase().getExamples().size();
		BitMatrix bitMatrix = new BitMatrix(numberExamples, clauses.size());
		for(int clause = 0; clause < clauses.size(); clause++)
			for(int i = 0; i < numberExamples; i++)
				bitMatrix.put(i, clause, clauses.get(clause).getValidity().get(i));
		return bitMatrix;
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
		BitMatrix bitMatrix = IdpExecutor.get().testValidityTheories(base);
		return new ValidityTable(bitMatrix, logicBase.getExamples());
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

	/**
	 * Returns the validity vector for the given example
	 * @param example	The example to return the values for
	 * @return	A vector of booleans, where return.get(i) == true indicates that the example is valid for the ith clause
	 */
	public BitVector getValidity(Example example) {
		if(!this.examples.containsKey(example))
			throw new IllegalArgumentException("Example not in validity table: " + example);
		return this.validity.part(this.examples.get(example), 0, 1, this.validity.rows()).toBitVector();
	}

	//endregion
}
