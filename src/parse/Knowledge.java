package parse;

import basic.ArrayUtil;
import basic.StringUtil;
import clausal_discovery.core.LogicBase;
import clausal_discovery.core.PredicateDefinition;
import clausal_discovery.instance.Instance;
import idp.IdpProgramPrinter;
import logic.bias.Type;
import logic.example.Example;
import logic.expression.formula.Clause;
import logic.expression.formula.Formula;
import logic.expression.formula.Predicate;
import logic.expression.term.Variable;
import logic.theory.Vocabulary;
import pair.TypePair;
import util.Numbers;
import util.Randomness;
import vector.SafeList;
import vector.Vector;

import java.util.*;
import java.util.stream.Collectors;

/**
* The knowledge class implements logic base, containing all the information from a logic file
 *
 * @author Samuel Kolb
*/
public class Knowledge implements LogicBase {

	private final Vocabulary vocabulary;

	@Override
	public Vocabulary getVocabulary() {
		return vocabulary;
	}

	private final SafeList<Example> examples;

	@Override
	public SafeList<Example> getExamples() {
		return examples;
	}

	private final SafeList<PredicateDefinition> searchList;

	@Override
	public SafeList<PredicateDefinition> getSearchList() {
		return searchList;
	}

	/**
	 * Creates a new knowledge instances with a vocabulary, examples and a list of search predicates
	 * @param vocabulary    The vocabulary
	 * @param examples      The list of examples
	 * @param searchList	The list of search predicates
	 */
	public Knowledge(Vocabulary vocabulary, SafeList<Example> examples, SafeList<PredicateDefinition> searchList) {
		this.vocabulary = vocabulary;
		this.examples = examples;
		this.searchList = searchList;
	}

	@Override
	public String toString() {
		return new IdpProgramPrinter.Cached().printVocabulary(getVocabulary(), "Vocabulary")
				+ StringUtil.join("\n", examples);
	}

	@Override
	public List<Formula> getBackgroundKnowledge() {
		// TODO add background knowledge
		List<Formula> formulas = new ArrayList<>();
		for(PredicateDefinition definition : getSearchList().filter(PredicateDefinition::isSymmetric)) {
			Predicate predicate = definition.getPredicate();
			Vector<Integer> variableIndices = ArrayUtil.wrap(Numbers.range(predicate.getArity() - 1));
			Map<Integer, Variable> variableMap = new HashMap<>();
			for(Integer index : variableIndices) {
				Type type = predicate.getTypes().get(index);
				variableMap.put(index, new Variable(type.getName() + index, type));
			}
			Instance body = new Instance(definition, variableIndices);
			List<Numbers.Permutation> permutations = Numbers.getPermutations(predicate.getArity());
			for(int i = 1; i < permutations.size(); i++) {
				Instance head = new Instance(definition, new Vector<>(permutations.get(i).getIntegerArray()));
				formulas.add(Clause.horn(head.makeAtom(variableMap), body.makeAtom(variableMap)));
			}
		}
		return formulas;
	}

	@Override
	public List<LogicBase> split() {
		return getExamples().stream()
				.map(example -> copy(new SafeList<>(example)))
				.collect(Collectors.toList());
	}

	@Override
	public LogicBase filterExamples(java.util.function.Predicate<Example> predicate) {
		SafeList<Example> examples = getExamples().filter(predicate);
		return copy(examples);
 	}

	@Override
	public TypePair<LogicBase> split(double fraction) {
		if(fraction < 0 || fraction > 1)
			throw new IllegalArgumentException(String.format("Fraction must be between 0 and 1, was %f", fraction));
		int size = getExamples().size();
		int index = Math.max(1, Math.min((int) Math.ceil(fraction * size), size - 1));
		List<Example> examples = new ArrayList<>(getExamples());
		Collections.shuffle(examples, Randomness.getRandom());
		SafeList<Example> safeList = SafeList.from(examples);
		return new TypePair.Implementation<>(copy(safeList.subList(0, index)), copy(safeList.subList(index, size)));
	}

	private Knowledge copy(SafeList<Example> examples) {
		return new Knowledge(getVocabulary(), examples, getSearchList());
	}
}
