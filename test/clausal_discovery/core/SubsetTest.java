package clausal_discovery.core;

import clausal_discovery.core.StatusClause;
import clausal_discovery.instance.InstanceList;
import clausal_discovery.instance.PositionedInstance;
import log.Log;
import logic.expression.formula.Predicate;
import org.junit.Test;
import vector.Vector;
import vector.WriteOnceVector;

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
	@Test
	public void testSubsetSymmetry_True() {
		Predicate n = new Predicate("n", true, 2);
		InstanceList list = new InstanceList(new Vector<Predicate>(n), 4);
		StatusClause accepted = getStatusClause(Arrays.asList(list.getInstance(0, true), list.getInstance(1, true)));
		StatusClause tested = getStatusClause(Arrays.asList(list.getInstance(0, true), list.getInstance(2, true)));
		assertTrue(accepted.equalsSymmetric(tested));
		assertTrue(accepted.isSubsetOf(tested));
	}

	private StatusClause getStatusClause(List<PositionedInstance> instances) {
		return StatusClause.buildClauseFromList(instances);
	}
	//endregion
}
