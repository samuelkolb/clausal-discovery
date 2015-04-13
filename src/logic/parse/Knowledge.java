package logic.parse;

import basic.StringUtil;
import logic.expression.formula.Predicate;
import vector.Vector;
import clausal_discovery.core.LogicBase;
import idp.IdpProgramPrinter;
import logic.example.Example;
import logic.theory.Vocabulary;

/**
* Created by samuelkolb on 22/02/15.
*/
public class Knowledge implements LogicBase {

	private final Vocabulary vocabulary;

	@Override
	public Vocabulary getVocabulary() {
		return vocabulary;
	}

	private final Vector<Example> examples;

	@Override
	public Vector<Example> getExamples() {
		return examples;
	}

	private final Vector<Predicate> searchPredicates;

	@Override
	public Vector<Predicate> getSearchPredicates() {
		return searchPredicates;
	}

	/**
	 * Creates a new knowledge instances with a vocabulary, examples and a list of search predicates
	 * @param vocabulary		The vocabulary
	 * @param examples			The list of examples
	 * @param searchPredicates	The list of search predicates
	 */
	public Knowledge(Vocabulary vocabulary, Vector<Example> examples, Vector<Predicate> searchPredicates) {
		this.vocabulary = vocabulary;
		this.examples = examples;
		this.searchPredicates = searchPredicates;
	}

	@Override
	public String toString() {
		return new IdpProgramPrinter().printVocabulary(getVocabulary(), "Vocabulary")
				+ StringUtil.join("\n", examples.getArray());
	}
}
