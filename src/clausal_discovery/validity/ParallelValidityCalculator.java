package clausal_discovery.validity;

import clausal_discovery.core.LogicBase;
import clausal_discovery.core.StatusClause;
import logic.expression.formula.Formula;
import logic.theory.*;
import vector.Vector;

import java.util.Map;
import java.util.concurrent.*;

/**
 * The parallel validity calculator starts calculating validity in parallel when a request is submitted. When the
 * validity status is queried it returns the calculated result or waits until it is available.
 *
 * @author Samuel Kolb
 */
public class ParallelValidityCalculator extends ValidityCalculator {

	private class CheckValidityCallable implements Callable<Vector<Boolean>> {

		private final Formula formula;

		private CheckValidityCallable(Formula formula) {
			this.formula = formula;
		}

		@Override
		public Vector<Boolean> call() throws Exception {
			Vector<Theory> theories = new Vector<>(getTheory(formula));
			return getExecutor().testValidityTheories(getKnowledgeBase(theories));
		}
	}

	//region Variables
	private final Map<Formula, Future<Boolean>> validityTable = new ConcurrentHashMap<>();

	private final ExecutorService executorService;
	//endregion

	//region Construction

	/**
	 * Creates a new parallel validity calculator
	 * @param base					The logic base
	 * @param executor				The executor to be used for validity tests
	 * @param backgroundTheories	The background theories
	 */
	public ParallelValidityCalculator(LogicBase base, LogicExecutor executor, Vector<Theory> backgroundTheories) {
		super(base, executor, backgroundTheories);
		this.executorService = Executors.newFixedThreadPool(8);
	}

	//endregion

	//region Public methods

	@Override
	public ValidatedClause getValidatedClause(StatusClause clause) {
		Formula formula = clause.getFormula();
		return new ValidatedClause(getBase(), clause, executorService.submit(new CheckValidityCallable(formula)));
	}

	@Override
	public void shutdown() {
		executorService.shutdownNow();
	}

	//endregion
}
