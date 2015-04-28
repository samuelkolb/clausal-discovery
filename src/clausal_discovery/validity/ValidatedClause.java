package clausal_discovery.validity;

import basic.ArrayUtil;
import clausal_discovery.core.LogicBase;
import clausal_discovery.core.StatusClause;
import vector.Vector;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Contains a clause as well as information about its logic base and validity.
 * Asking for validity values may pause the current thread to await the computation of these values.
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
			Vector<Boolean> vector = this.validity.get();
			if(vector.size() != getLogicBase().getExamples().size())
				throw new IllegalArgumentException(String.format("Expected %d values, got %d", getLogicBase().getExamples().size(), vector.size()));
			return vector;
		} catch(InterruptedException | ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}

	private Optional<Integer> validCount = Optional.empty();

	public int getValidCount() {
		if(!validCount.isPresent())
			validCount = Optional.of(count(getValidity()));
		return validCount.get();
	}

	//endregion

	//region Construction

	/**
	 * Creates a new validated clause with an empty status clause and all false validity values
	 * @param logicBase	The logic base
	 */
	public ValidatedClause(LogicBase logicBase) {
		this(logicBase, new StatusClause(), Vector.create(ArrayUtil.fill(logicBase.getExamples().size(), false)));
	}

	/**
	 * Creates a validated clause
	 * @param logicBase	The logic base
	 * @param clause	The status clause
	 * @param validity	The validity values
	 */
	public ValidatedClause(LogicBase logicBase, StatusClause clause, Vector<Boolean> validity) {
		this(logicBase, clause, CompletableFuture.completedFuture(validity));
	}

	ValidatedClause(LogicBase logicBase, StatusClause clause, Future<Vector<Boolean>> validity) {
		this.logicBase = logicBase;
		this.clause = clause;
		this.validity = validity;
	}

	//endregion

	//region Public methods

	/**
	 * Returns whether this clause covers all examples
	 * @return	True iff this clause covers all examples in its logic base
	 */
	public boolean coversAll() {
		return getValidCount() == getLogicBase().getExamples().size();
	}

	@Override
	public String toString() {
		return getClause().toString();
	}

	//endregion

	private int count(Vector<Boolean> validity) {
		int count = 0;
		for(Boolean bool : validity)
			if(bool)
				count++;
		return count;
	}
}
