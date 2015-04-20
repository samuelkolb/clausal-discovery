package clausal_discovery.validity;

import clausal_discovery.core.LogicBase;
import log.Log;
import logic.expression.formula.Formula;
import logic.theory.*;
import vector.Vector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This calculator acts similar as the batch validity calculator but processes all stored validity requests in parallel
 * instead of executing them in one call.
 *
 * @author Samuel Kolb
 */
public class BatchParallelValidityCalculator extends BatchValidityCalculator {

	private class CheckValidRunnable implements Runnable {

		private final Formula formula;

		private CheckValidRunnable(Formula formula) {
			this.formula = formula;
		}

		@Override
		public void run() {
			Vector<Theory> theories = new Vector<Theory>(getTheory(formula));
			getValidityTable().put(formula, getExecutor().testValidityTheory(getKnowledgeBase(theories)));
		}
	}

	//region Variables

	//endregion

	//region Construction

	/**
	 * Creates a new batch-parallel validity calculator
	 * @param base					The logic base
	 * @param executor				The executor to be used for validity tests
	 * @param backgroundTheories	The background theories
	 */
	public BatchParallelValidityCalculator(LogicBase base, LogicExecutor executor, Vector<Theory> backgroundTheories) {
		super(base, executor, backgroundTheories);
	}

	//endregion

	//region Public methods

	@Override
	void extendValidityTable() {
		Log.LOG.printLine("Calculating...");
		ExecutorService executorService = Executors.newFixedThreadPool(8);
		for(Formula formula : getFormulas())
			executorService.execute(new CheckValidRunnable(formula));
		getFormulas().clear();
		executorService.shutdown();
		try {
			executorService.awaitTermination(10, TimeUnit.DAYS);
		} catch(InterruptedException e) {
			throw new IllegalStateException(e);
		}
		Log.LOG.printLine("...Done");
	}

	//endregion
}
