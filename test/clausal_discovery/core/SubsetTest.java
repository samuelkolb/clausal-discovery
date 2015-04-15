package clausal_discovery.core;

import clausal_discovery.instance.InstanceList;
import clausal_discovery.instance.PositionedInstance;
import logic.expression.formula.Predicate;
import org.junit.Test;
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
	 * @param instances	The instances to add to the clause
	 * @return	A clause
	 */
	public static StatusClause buildClauseFromList(List<PositionedInstance> instances) {
		StatusClause clause = new StatusClause();
		for(PositionedInstance instance : instances)
			clause = clause.addIfValid(instance).get();
		return clause;
	}

	@Test
	public void testSubsetSymmetry_True() {
		PredicateDefinition definition = new PredicateDefinition(new Predicate("n", 2), true, false);
		InstanceList list = new InstanceList(new Vector<PredicateDefinition>(definition), 4);
		StatusClause accepted = getStatusClause(Arrays.asList(list.getInstance(0, true), list.getInstance(1, true)));
		StatusClause tested = getStatusClause(Arrays.asList(list.getInstance(0, true), list.getInstance(2, true)));
		assertTrue(accepted.equalsSymmetric(tested));
		assertTrue(accepted.isSubsetOf(tested));
	}

	private StatusClause getStatusClause(List<PositionedInstance> instances) {
		return buildClauseFromList(instances);
	}
	//endregion
}
