package clausal_discovery.validity;

import cern.colt.bitvector.BitMatrix;
import cern.colt.bitvector.BitVector;
import clausal_discovery.core.LogicBase;
import clausal_discovery.core.StatusClause;
import logic.expression.formula.Formula;
import logic.theory.LogicExecutor;
import logic.theory.Theory;
import vector.SafeList;
import vector.Vector;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The parallel validity calculator starts calculating validity in parallel when a request is submitted. When the
 * validity status is queried it returns the calculated result or waits until it is available.
 *
 * @author Samuel Kolb
 */
public class ParallelValidityCalculator extends ValidityCalculator {

	private class CheckValidityCallable implements Callable<BitVector> {

		private final Formula formula;

		private CheckValidityCallable(Formula formula) {
			this.formula = formula;
		}

		@Override
		public BitVector call() throws Exception {
			SafeList<Theory> theories = new SafeList<>(getTheory(formula));
			BitMatrix matrix = getExecutor().testValidityTheories(getKnowledgeBase(theories));
			return matrix.part(0, 0, matrix.columns(), 1).toBitVector();
		}
	}

	//region Variables
	private final ExecutorService executorService;
	//endregion

	//region Construction

	/**
	 * Creates a new parallel validity calculator
	 * @param base					The logic base
	 * @param executor				The executor to be used for validity tests
	 * @param backgroundTheories	The background theories
	 */
	public ParallelValidityCalculator(LogicBase base, LogicExecutor executor, SafeList<Theory> backgroundTheories) {
		super(base, executor, backgroundTheories);
		this.executorService = Executors.newFixedThreadPool(16);
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
