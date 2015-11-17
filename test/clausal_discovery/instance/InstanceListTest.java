package clausal_discovery.instance;

import clausal_discovery.core.PredicateDefinition;
import logic.expression.formula.Predicate;
import org.junit.BeforeClass;
import org.junit.Test;
import vector.SafeList;
import vector.Vector;

import java.util.ArrayList;
import java.util.Comparator;
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
		symmetric = new PredicateDefinition(new Predicate("s", 3));
	}

	@Test
	public void testCreationScenario() {
		InstanceList list = new InstanceList(new Vector<>(binary1), 3);
		/*String string = "type O\npred b1(O,O)\nexample 1 {\n\tconst O O1 O2\n\tb1(O1,O1)\n\tb1(O2,O1)\n}\n";
		Log.LOG.saveState();
		RunClient client = new RunClient();
		Log.LOG.revert();
		Configuration configuration = new Configuration(new LogicParser().parse(string), new Vector<>(), 2, 2);
		client.run(configuration);*/
		List<Instance> instances = create(binary1, new int[]{0, 0, 0, 1, 1, 0, 0, 2, 2, 0, 1, 1, 1, 2, 2, 1, 2, 2});
		for(int i = 0; i < Math.min(list.size(), instances.size()); i++) {
			assertEquals(instances.get(i), list.get(i));
		}
	}

	private List<Instance> create(PredicateDefinition definition, int[] variables) {
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
}