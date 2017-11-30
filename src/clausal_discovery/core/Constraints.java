package clausal_discovery.core;

import clausal_discovery.core.score.ClauseFunction;
import clausal_discovery.validity.ValidityTable;
import logic.expression.formula.Formula;
import vector.SafeList;

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

	private final SafeList<Formula> hardConstraints;

	public SafeList<Formula> getHardConstraints() {
		return hardConstraints;
	}

	private final SafeList<Formula> softConstraints;

	public SafeList<Formula> getSoftConstraints() {
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
		this(logicBase, new SafeList<>(hardConstraints), new SafeList<>(softConstraints),
				new SafeList<>(weights));
	}

	public Constraints(LogicBase logicBase, SafeList<Formula> hardConstraints, SafeList<Formula> softConstraints,
					   SafeList<Double> weights) {
		this.logicBase = logicBase;
		this.hardConstraints = hardConstraints;
		this.softConstraints = softConstraints;
		this.clauseFunction = new ClauseFunction(new SafeList<>(weights), ValidityTable.create(logicBase,
				new ArrayList<>(softConstraints)));
	}


	//endregion

	//region Public methods

	//endregion
}
