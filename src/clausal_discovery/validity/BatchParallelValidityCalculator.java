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

		private final Vector<Structure> structures;

		private CheckValidRunnable(Formula formula, Vector<Structure> structures) {
			this.formula = formula;
			this.structures = structures;
		}

		@Override
		public void run() {
			Vector<Theory> theories = new Vector<Theory>(getTheory(formula));
			KnowledgeBase program = new KnowledgeBase(getBase().getVocabulary(), theories, structures);
			getValidityTable().put(formula, getExecutor().isValid(program));
		}
	}

	//region Variables

	//endregion

	//region Construction

	/**
	 * Creates a new batch-parallel validity calculator
	 * @param base		The logic base
	 * @param executor	The executor to be used for validity tests
	 */
	public BatchParallelValidityCalculator(LogicBase base, LogicExecutor executor) {
		super(base, executor);
	}

	//endregion

	//region Public methods

	@Override
	void extendValidityTable() {
		Log.LOG.printLine("Calculating...");
		ExecutorService executorService = Executors.newFixedThreadPool(8);
		for(Formula formula : getFormulas())
			executorService.execute(new CheckValidRunnable(formula, getStructures()));
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
