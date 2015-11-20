package clausal_discovery.core;

import logic.expression.formula.Formula;
import pair.TypePair;
import vector.SafeList;
import vector.Vector;
import logic.example.Example;
import logic.theory.Vocabulary;

import java.util.List;
import java.util.function.Predicate;

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
	SafeList<Example> getExamples();

	/**
	 * Returns the predicates that can be used in clauses
	 * @return	A list of predicates
	 */
	SafeList<PredicateDefinition> getSearchList();

	/**
	 * Returns the formulas that describe the background knowledge in this logic base.
	 * @return	A list of formulas
	 */
	List<Formula> getBackgroundKnowledge();

	/**
	 * Splits the logic base into a list of logic bases containing one example each
	 * @return	A list of logic bases, each of which contains one example
	 */
	List<LogicBase> split();

	/**
	 * Creates a new logic base that only contains the accepted examples
	 * @param predicate	The predicate acting as filter
	 * @return	A new logic base
	 */
	LogicBase filterExamples(Predicate<Example> predicate);

	/**
	 * Splits the logic base into two
	 * @param fraction	The fraction of examples to retain in the first new logic base (bewteen 0 and 1)
	 * @return	A pair of logic-bases
	 */
	TypePair<LogicBase> split(double fraction);
}
