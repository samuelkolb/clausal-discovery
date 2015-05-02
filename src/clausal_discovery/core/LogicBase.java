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
	Vocabulary getVocabulary();

	/**
	 * Returns the examples that can be used to validate clauses
	 * @return	A list of examples
	 */
	Vector<Example> getExamples();

	/**
	 * Returns the predicates that can be used in clauses
	 * @return	A list of predicates
	 */
	Vector<PredicateDefinition> getSearchPredicates();

	/**
	 * Returns the formulas that describe the symmetries of predicates in this logic base
	 * @return	A list of formulas
	 */
	List<Formula> getSymmetryFormulas();

	/**
	 * Splits the logic base into a list of logic bases containing one example each
	 * @return	A list of logic bases, each of which contains one example
	 */
	List<LogicBase> split();
}
