package clausal_discovery.core.score;

import basic.ArrayUtil;
import clausal_discovery.core.LogicBase;
import clausal_discovery.core.StatusClause;
import clausal_discovery.validity.ValidatedClause;
import clausal_discovery.validity.ValidityTable;
import logic.theory.Theory;
import pair.Pair;
import vector.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuelkolb on 01/05/15.
 *
 * @author Samuel Kolb
 */
public class StatusClauseFunction extends ClauseFunction {

	private final Vector<StatusClause> clauses;

	public Vector<StatusClause> getClauses() {
		return clauses;
	}

	/**
	 * Creates a status clause function
	 *
	 * @param clauses       The status clauses
	 * @param weights       The weights per clause
	 * @param validityTable The validity table
	 */
	public StatusClauseFunction(Vector<StatusClause> clauses, Vector<Double> weights, ValidityTable validityTable) {
		super(weights, validityTable);
		if(weights.size() != clauses.size())
			throw new IllegalArgumentException(String.format("Number of weights and clauses do not match, %d vs %d",
					weights.size(), clauses.size()));
		this.clauses = clauses;
	}

	/**
	 * Creates a copy of this scoring function
	 *
	 * @param logicBase          The logic base to use in the copy
	 * @param backgroundTheories The background knowledge to use
	 * @return A copy with a new validity table
	 */
	public StatusClauseFunction copy(LogicBase logicBase, Vector<Theory> backgroundTheories) {
		ValidityTable validityTable = ValidityTable.create(logicBase, backgroundTheories, getClauses());
		return new StatusClauseFunction(getClauses(), getWeights(), validityTable);
	}

	/*public StatusClauseFunction leaveOut(int index) {
		return new StatusClauseFunction(getClauses().leaveOut(index), getWeights().leaveOut(index), getValidity().removeClause(index));
	}*/
}
