package clausal_discovery.instance;

import clausal_discovery.core.PredicateDefinition;
import log.Log;
import logic.expression.formula.Predicate;
import org.junit.BeforeClass;
import org.junit.Test;
import vector.SafeList;
import vector.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests the instance list implementation.
 *
 * @author Samuel Kolb
 */
public class InstanceListTest {

	private static PredicateDefinition unary1;

	private static PredicateDefinition unary2;

	private static PredicateDefinition binary1;

	private static PredicateDefinition binary2;

	private static PredicateDefinition tertiary;

	private static PredicateDefinition symmetric;

	@BeforeClass
	public static void before() {
		unary1 = new PredicateDefinition(new Predicate("u1", 1));
		unary2 = new PredicateDefinition(new Predicate("u2", 1));
		binary1 = new PredicateDefinition(new Predicate("b1", 2));
		binary2 = new PredicateDefinition(new Predicate("b2", 2));
		tertiary = new PredicateDefinition(new Predicate("t", 3));
		symmetric = new PredicateDefinition(new Predicate("s", 3), true, false);
	}

	@Test
	public void testCreationScenario() {
		InstanceList list = new InstanceList(new Vector<>(unary1, unary2, binary1, binary2, tertiary), 3);
		// 0, 0-1, 0-1-2, 0-2, 1, 1-2, 2
		// region instances
		List<Instance> instances = combine(Arrays.asList(
				create(unary1, 0),
				create(unary2, 0),
				create(binary1, 0, 0),
				create(binary2, 0, 0),
				create(tertiary, 0, 0, 0),
				create(tertiary, 0, 0, 1),
				create(binary1, 0, 1),
				create(binary2, 0, 1),
				create(tertiary, 0, 1, 0),
				create(tertiary, 0, 1, 1),
				create(binary1, 1, 0),
				create(binary2, 1, 0),
				create(tertiary, 1, 0, 0),
				create(tertiary, 1, 0, 1),
				create(tertiary, 1, 1, 0),
				create(tertiary, 0, 1, 2),
				create(tertiary, 0, 2, 1),
				create(tertiary, 1, 0, 2),
				create(tertiary, 1, 2, 0),
				create(tertiary, 2, 0, 1),
				create(tertiary, 2, 1, 0),
				create(tertiary, 0, 0, 2),
				create(binary1, 0, 2),
				create(binary2, 0, 2),
				create(tertiary, 0, 2, 0),
				create(tertiary, 0, 2, 2),
				create(binary1, 2, 0),
				create(binary2, 2, 0),
				create(tertiary, 2, 0, 0),
				create(tertiary, 2, 0, 2),
				create(tertiary, 2, 2, 0),
				create(unary1, 1),
				create(unary2, 1),
				create(binary1, 1, 1),
				create(binary2, 1, 1),
				create(tertiary, 1, 1, 1),
				create(tertiary, 1, 1, 2),
				create(binary1, 1, 2),
				create(binary2, 1, 2),
				create(tertiary, 1, 2, 1),
				create(tertiary, 1, 2, 2),
				create(binary1, 2, 1),
				create(binary2, 2, 1),
				create(tertiary, 2, 1, 1),
				create(tertiary, 2, 1, 2),
				create(tertiary, 2, 2, 1),
				create(unary1, 2),
				create(unary2, 2),
				create(binary1, 2, 2),
				create(binary2, 2, 2),
				create(tertiary, 2, 2, 2)
		));
		// endregion instances
		for(int i = 0; i < Math.min(list.size(), instances.size()); i++) {
			try {
				assertEquals(instances.get(i), list.get(i));
			} catch(AssertionError error) {
				throw new AssertionError("Wrongful entry " + i + error.getMessage(), error);
			}
		}
	}

	@Test
	public void testCreationSymmetricScenario() {
		InstanceList list = new InstanceList(new Vector<>(symmetric), 4);
		Log.LOG.printLine(list);
		// 0, 0-1, 0-1-2, 0-1-3, 0-2, 0-2-3, 0-3 1, 1-2, 1-2-3, 1-3, 2, 2-3, 3
		// region instances
		List<Instance> instances = combine(Arrays.asList(
				create(symmetric, 0, 0, 0),
				create(symmetric, 0, 0, 1),
				create(symmetric, 0, 1, 1),
				create(symmetric, 0, 1, 2),
				create(symmetric, 0, 1, 3),
				create(symmetric, 0, 0, 2),
				create(symmetric, 0, 2, 2),
				create(symmetric, 0, 2, 3),
				create(symmetric, 0, 0, 3),
				create(symmetric, 0, 3, 3),
				create(symmetric, 1, 1, 1),
				create(symmetric, 1, 1, 2),
				create(symmetric, 1, 2, 2),
				create(symmetric, 1, 2, 3),
				create(symmetric, 1, 1, 3),
				create(symmetric, 1, 3, 3),
				create(symmetric, 2, 2, 2),
				create(symmetric, 2, 2, 3),
				create(symmetric, 2, 3, 3),
				create(symmetric, 3, 3, 3)
		));
		// endregion instances
		for(int i = 0; i < Math.min(list.size(), instances.size()); i++) {
			try {
				assertEquals(instances.get(i), list.get(i));
			} catch(AssertionError error) {
				throw new AssertionError("Wrongful entry " + i + error.getMessage(), error);
			}
		}
	}

	private List<Instance> create(PredicateDefinition definition, int... variables) {
		if(variables.length % definition.getArity() != 0) {
			throw new IllegalArgumentException("Amount of variables incorrect (" + variables.length + ")");
		}
		List<Instance> instances = new ArrayList<>(variables.length / definition.getArity());
		for(int i = 0; i < variables.length / definition.getArity(); i++) {
			int[] array = new int[definition.getArity()];
			System.arraycopy(variables, i * definition.getArity(), array, 0, definition.getArity());
			instances.add(new Instance(definition, Vector.create(array)));
		}
		return instances;
	}

	private List<Instance> combine(List<List<Instance>> nestedLists) {
		List<Instance> instances = new ArrayList<>();
		for(List<Instance> list : nestedLists) {
			for(Instance instance : list) {
				instances.add(instance);
			}
		}
		return instances;
	}
}