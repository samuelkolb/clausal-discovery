package clausal_discovery.validity;

import clausal_discovery.core.LogicBase;
import clausal_discovery.core.StatusClause;
import logic.example.Example;
import logic.expression.formula.Formula;
import logic.theory.*;
import vector.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * The abstract validity calculator encapsulates different (potentially concurrent) implementations to calculate
 * validity for clauses.
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
	 * Returns a validated clause for the given clause
	 * @param statusClause	The status clause to calculate validity for
	 * @return	A validated clause
	 */
	public abstract ValidatedClause getValidatedClause(StatusClause statusClause);

	/**
	 * Free any retained resources
	 */
	public void shutdown() {

	}

	Vector<Structure> getStructures() {
		return new Vector<>(Structure.class, getBase().getExamples(), Example::getStructure);
	}

	protected Theory getTheory(Formula formula) {
		List<Formula> formulas = new ArrayList<>();
		formulas.add(formula);
		//formulas.addAll(getBase().getSymmetryFormulas()); // TODO
		return new InlineTheory(formulas);
	}

	protected KnowledgeBase getKnowledgeBase(Vector<Theory> theories) {
		return new KnowledgeBase(getBase().getVocabulary(), theories, getBackgroundTheories(), getStructures());
	}

	//endregion
}
