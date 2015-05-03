package clausal_discovery.core;

import clausal_discovery.configuration.Configuration;
import clausal_discovery.core.score.ClauseFunction;
import clausal_discovery.validity.ValidityTable;
import logic.expression.formula.Formula;
import vector.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuelkolb on 02/05/15.
 *
 * @author Samuel Kolb
 */
public class Constraints {

	//region Variables

	private final LogicBase logicBase;

	public LogicBase getLogicBase() {
		return logicBase;
	}

	private final Vector<Formula> hardConstraints;

	public Vector<Formula> getHardConstraints() {
		return hardConstraints;
	}

	private final Vector<Formula> softConstraints;

	public Vector<Formula> getSoftConstraints() {
		return softConstraints;
	}

	private final ClauseFunction clauseFunction;

	public ClauseFunction getClauseFunction() {
		return clauseFunction;
	}

	//endregion

	//region Construction

	public Constraints(LogicBase logicBase, List<Formula> hardConstraints, List<Formula> softConstraints,
					   List<Double> weights) {
		this(logicBase, new Vector<>(Formula.class, hardConstraints), new Vector<>(Formula.class, softConstraints),
				new Vector<>(Double.class, weights));
	}

	public Constraints(LogicBase logicBase, Vector<Formula> hardConstraints, Vector<Formula> softConstraints,
					   Vector<Double> weights) {
		this.logicBase = logicBase;
		this.hardConstraints = hardConstraints;
		this.softConstraints = softConstraints;
		this.clauseFunction = new ClauseFunction(weights, ValidityTable.create(logicBase, new ArrayList<>(softConstraints)));
	}


	//endregion

	//region Public methods

	//endregion
}
