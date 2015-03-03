package clausal_discovery;

import idp.IdpExpressionPrinter;
import log.Log;
import logic.example.Example;
import logic.expression.formula.Formula;
import logic.theory.LogicExecutor;
import logic.theory.LogicProgram;
import logic.theory.Structure;
import logic.theory.Theory;
import vector.*;
import vector.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by samuelkolb on 02/03/15.
 *
 * @author Samuel Kolb
 */
public abstract class ValidityCalculator {

	//region Variables

	private final LogicBase base;

	LogicBase getBase() {
		return base;
	}

	private final LogicExecutor executor;

	LogicExecutor getExecutor() {
		return executor;
	}

	//endregion

	//region Construction

	ValidityCalculator(LogicBase base, LogicExecutor executor) {
		this.base = base;
		this.executor = executor;
	}

	//endregion

	//region Public methods

	/**
	 * Submits the given formula, making it available to be queried later
	 * @param formula	The formula for which the validity will be queried
	 */
	public abstract void submitFormula(Formula formula);

	/**
	 * Returns whether the given formula is valid or not with respect to the given logic base
	 * @param formula	The formula for which the validity is to be returned
	 * @return	True iff the given formula is valid according to the provided logic executor
	 */
	public abstract boolean isValid(Formula formula);

	/**
	 * Free any retained resources
	 */
	public void shutdown() {

	}

	Vector<Structure> getStructures() {
		Vector<Structure> structures = new WriteOnceVector<>(new Structure[getBase().getExamples().size()]);
		for(Example example : getBase().getExamples())
			structures.add(example.getStructure());
		return structures;
	}

	//endregion
}
