package clausal_discovery.core;

import logic.expression.formula.Predicate;
import vector.Vector;
import logic.example.Example;
import logic.theory.Vocabulary;

/**
 * Created by samuelkolb on 05/12/14.
 *
 * @author Samuel Kolb
 */
public interface LogicBase {

	public Vocabulary getVocabulary();

	public Vector<Example> getExamples();

	public Vector<Predicate> getSearchPredicates();
}
