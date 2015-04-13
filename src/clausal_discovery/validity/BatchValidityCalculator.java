package clausal_discovery.validity;

import clausal_discovery.core.LogicBase;
import idp.IdpExpressionPrinter;
import log.Log;
import logic.expression.formula.Formula;
import logic.theory.LogicExecutor;
import logic.theory.LogicProgram;
import logic.theory.Theory;
import vector.Vector;
import vector.WriteOnceVector;

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
	 * @param base		The logic base
	 * @param executor	The executor to be used for validity tests
	 */
	public BatchValidityCalculator(LogicBase base, LogicExecutor executor) {
		super(base, executor);
	}

	//endregion

	//region Public methods


	@Override
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
	}

	void extendValidityTable() {
		Log.LOG.printLine("Calculating...");
		Vector<Theory> theories = new WriteOnceVector<>(new Theory[formulas.size()]);
		for(Formula formula : formulas)
			theories.add(new Theory(formula));
		LogicProgram program = new LogicProgram(getBase().getVocabulary(), theories, getStructures());
		boolean[] validity = getExecutor().areValid(program);
		for(int i = 0; i < formulas.size(); i++)
			validityTable.put(formulas.get(i), validity[i]);
		formulas.clear();
		Log.LOG.printLine("...Done");
	}
	//endregion
}
