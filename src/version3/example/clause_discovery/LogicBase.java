package version3.example.clause_discovery;

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
}
