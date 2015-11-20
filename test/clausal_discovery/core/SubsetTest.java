package clausal_discovery.core;

import clausal_discovery.instance.InstanceList;
import clausal_discovery.instance.PositionedInstance;
import log.Log;
import logic.expression.formula.Predicate;
import org.junit.Test;
import vector.SafeList;
import vector.Vector;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Created by samuelkolb on 14/04/15.
 *
 * @author Samuel Kolb
 */
public class SubsetTest {

	//region Variables

	//endregion

	//region Construction

	//endregion

	//region Public methods

	/**
	 * Creates a clause from list (incrementally builds a clause)
	 *
	 * @param instanceList	The instance list
	 * @param instances    	The instances to add to the clause
	 * @return	A clause
	 */
	public static StatusClause buildClauseFromList(InstanceList instanceList, List<PositionedInstance> instances) {
		StatusClause clause = new StatusClause(instanceList);
		for(PositionedInstance instance : instances)
			clause = clause.addIfValid(instance).get();
		return clause;
	}

	@Test
	public void testSubsetSymmetry_True() {
		// TODO second clause does not introduce variables in order
		PredicateDefinition definition = new PredicateDefinition(new Predicate("n", 2), true, false);
		InstanceList list = new InstanceList(new SafeList<>(definition), 4);
		StatusClause accepted = getStatusClause(list, Arrays.asList(list.getInstance(0, true), list.getInstance(1, true)));
		Log.LOG.printLine(accepted);
		Log.LOG.printLine(list.getInstance(0, true));
		Log.LOG.printLine(list.getInstance(2, true));
		StatusClause tested = getStatusClause(list, Arrays.asList(list.getInstance(0, true), list.getInstance(2, true)));
		Log.LOG.printLine(tested);
		assertTrue(accepted.equalsSymmetric(tested));
		assertTrue(accepted.isSubsetOf(tested));
	}

	private StatusClause getStatusClause(InstanceList instanceList, List<PositionedInstance> instances) {
		return buildClauseFromList(instanceList, instances);
	}
	//endregion
}
