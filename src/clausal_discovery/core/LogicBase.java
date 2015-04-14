package clausal_discovery.core;

import logic.expression.formula.Formula;
import logic.expression.formula.Predicate;
import vector.Vector;
import logic.example.Example;
import logic.theory.Vocabulary;

import java.util.List;

/**
 * Created by samuelkolb on 05/12/14.
 *
 * @author Samuel Kolb
 */
public interface LogicBase {

	/**
	 * Returns the vocabulary containing all predicates
	 * @return	A vocabulary
	 */
	public Vocabulary getVocabulary();

	/**
	 * Returns the examples that can be used to validate clauses
	 * @return	A list of examples
	 */
	public Vector<Example> getExamples();

	/**
	 * Returns the predicates that can be used in clauses
	 * @return	A list of predicates
	 */
	public Vector<Predicate> getSearchPredicates();

	/**
	 * Returns the formulas that describe the symmetries of predicates in this logic base
	 * @return	A list of formulas
	 */
	public List<Formula> getSymmetryFormulas();
}
