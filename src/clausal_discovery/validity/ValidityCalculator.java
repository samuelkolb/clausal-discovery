package clausal_discovery.validity;

import clausal_discovery.core.LogicBase;
import clausal_discovery.core.StatusClause;
import logic.example.Example;
import logic.expression.formula.Formula;
import logic.theory.*;
import vector.Vector;
import vector.WriteOnceVector;

import java.util.ArrayList;
import java.util.List;

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

	private final Vector<Theory> backgroundTheories;

	Vector<Theory> getBackgroundTheories() {
		return backgroundTheories;
	}

	//endregion

	//region Construction

	ValidityCalculator(LogicBase base, LogicExecutor executor, Vector<Theory> backgroundTheories) {
		this.base = base;
		this.executor = executor;
		this.backgroundTheories = backgroundTheories;
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
	 * Returns a validated clause for the given clause
	 * @param statusClause	The status clause to calculate validty for
	 * @return	A validated clause
	 */
	public abstract ValidatedClause getValidatedClause(StatusClause statusClause);

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

	protected Theory getTheory(Formula formula) {
		List<Formula> formulas = new ArrayList<>();
		formulas.add(formula);
		//formulas.addAll(getBase().getSymmetryFormulas());
		return new InlineTheory(formulas);
	}

	protected KnowledgeBase getKnowledgeBase(Vector<Theory> theories) {
		return new KnowledgeBase(getBase().getVocabulary(), theories, getBackgroundTheories(), getStructures());
	}

	//endregion
}
