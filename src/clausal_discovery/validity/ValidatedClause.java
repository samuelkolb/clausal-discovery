package clausal_discovery.validity;

import clausal_discovery.core.LogicBase;
import clausal_discovery.core.StatusClause;
import vector.Vector;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * Created by samuelkolb on 28/04/15.
 *
 * @author Samuel Kolb
 */
public class ValidatedClause {

	//region Variables

	private final LogicBase logicBase;

	public LogicBase getLogicBase() {
		return logicBase;
	}

	private final StatusClause clause;

	public StatusClause getClause() {
		return clause;
	}

	private final Future<Vector<Boolean>> validity;

	public Vector<Boolean> getValidity() {
		try {
			return this.validity.get();
		} catch(InterruptedException | ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}

	//endregion

	//region Construction

	public ValidatedClause(LogicBase logicBase, StatusClause clause, Future<Vector<Boolean>> validity) {
		this.logicBase = logicBase;
		this.clause = clause;
		this.validity = validity;
	}

	//endregion

	//region Public methods

	//endregion
}
