package clausal_discovery.validity;

import clausal_discovery.core.LogicBase;
import clausal_discovery.core.StatusClause;
import logic.expression.formula.Formula;
import logic.theory.LogicExecutor;
import logic.theory.Theory;
import vector.SafeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The batch validity calculator stores validity requests and executes them in one batch when an unknown validity value
 * is queried.
 *
 * @author Samuel Kolb
 */
public class BatchValidityCalculator extends ValidityCalculator {

	//region Variables
	private final List<Formula> formulas = new ArrayList<>();

	List<Formula> getFormulas() {
		return formulas;
	}

	private final Map<Formula, Boolean> validityTable = new ConcurrentHashMap<>();

	protected Map<Formula, Boolean> getValidityTable() {
		return validityTable;
	}
	//endregion

	//region Construction

	/**
	 * Creates a new batch validity calculator
	 * @param base					The logic base
	 * @param executor				The executor to be used for validity tests
	 * @param backgroundTheories	The background theories
	 */
	public BatchValidityCalculator(LogicBase base, LogicExecutor executor, SafeList<Theory> backgroundTheories) {
		super(base, executor, backgroundTheories);
	}

	//endregion

	//region Public methods


	/*@Override
	public void submitFormula(Formula formula) {
		Log.LOG.printLine("INFO Submitted " + IdpExpressionPrinter.print(formula));
		if(!validityTable.containsKey(formula))
			formulas.add(formula);
	}

	@Override
	public boolean isValid(Formula formula) {
		Log.LOG.printLine("INFO Is valid? " + IdpExpressionPrinter.print(formula));
		if(validityTable.containsKey(formula))
			return validityTable.get(formula);
		extendValidityTable();
		return validityTable.get(formula);
	}*/

	@Override
	public ValidatedClause getValidatedClause(StatusClause statusClause) {
		return null; // TODO
	}

	void extendValidityTable() {
		/*Log.LOG.printLine("Calculating...");
		SafeList<Theory> theories = new WriteOnceSafeList<>(new Theory[formulas.size()]);
		for(Formula formula : formulas)
			theories.add(getTheory(formula));
		SafeList<Boolean> validity = getExecutor().testValidityTheories(getKnowledgeBase(theories)).get(0);
		for(int i = 0; i < formulas.size(); i++)
			validityTable.put(formulas.get(i), validity.get(i));
		formulas.clear();
		Log.LOG.printLine("...Done");*/
	}

	//endregion
}
