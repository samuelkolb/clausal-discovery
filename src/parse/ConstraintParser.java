package parse;

import clausal_discovery.core.Constraints;
import clausal_discovery.core.LogicBase;
import clausal_discovery.core.StatusClauseConverter;
import clausal_discovery.instance.Instance;
import log.Log;
import logic.expression.formula.Formula;
import logic.theory.Vocabulary;
import time.Stopwatch;
import vector.SafeList;
import vector.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuelkolb on 02/05/15.
 *
 * @author Samuel Kolb
 */
public class ConstraintParser implements LocalParser<Constraints> {

	private class ParserState {

		private final List<Formula> hardConstraints = new ArrayList<>();

		public void addHardConstraint(Formula formula) {
			this.hardConstraints.add(formula);
		}

		private final List<Formula> softConstraints = new ArrayList<>();

		private final List<Double> weights = new ArrayList<>();

		public void addSoftConstraint(Double weight, Formula formula) {
			this.softConstraints.add(formula);
			this.weights.add(weight);
		}

		public Constraints getConstraints() {
			return new Constraints(getLogicBase(), hardConstraints, softConstraints, weights);
		}
	}

	private class SoftConstraintParser extends MatchParser<ParserState> {

		@Override
		public boolean matches(String string, ParserState parseState) throws ParsingError {
			if(!string.matches(".+:.+\\n"))
				return false;
			String[] parts = string.trim().split("\\s*:\\s*");
			Formula clause = parseClause(getLogicBase().getVocabulary(), parts[1]);
			parseState.addSoftConstraint(Double.parseDouble(parts[0]), clause);
			return true;
		}

	}

	private class HardConstraintParser extends MatchParser<ParserState> {

		@Override
		public boolean matches(String string, ParserState parseState) throws ParsingError {
			if(!string.matches(".+\\n"))
				return false;
			parseState.addHardConstraint(parseClause(getLogicBase().getVocabulary(), string.trim()));
			return true;
		}

	}

	//region Variables

	private final LogicBase logicBase;

	private LogicBase getLogicBase() {
		return logicBase;
	}

	//endregion

	//region Construction

	/**
	 * Creates a new constraint parser
	 * @param logicBase	The logic base to use for building constraints
	 */
	public ConstraintParser(LogicBase logicBase) {
		this.logicBase = logicBase;
	}


	//endregion

	//region Public methods

	@Override
	public Constraints parse(String content) {
		Stopwatch stopwatch = new Stopwatch(true);
		List<ScopeParser<ParserState>> parsers = new ArrayList<>();
		parsers.add(new SoftConstraintParser());
		parsers.add(new HardConstraintParser());
		ParserState parse = new BaseScopeParser<>(parsers).parse(new ParseCursor(content), new ParserState());
		Log.LOG.formatLine("INFO Parsing constraints took %.2f seconds", stopwatch.stop() / 1000);
		return parse.getConstraints();
	}

	public static Formula parseClause(Vocabulary vocabulary, String string) {
		String[] twoParts = string.split(" => ");
		String[] bodyParts = twoParts[0].split(" & ");
		String[] headParts = twoParts[1].split(" | ");
		List<Instance> bodyInstances = new ArrayList<>();
		for(String bodyPart : bodyParts)
			bodyInstances.add(parseInstance(vocabulary, bodyPart));
		List<Instance> headInstances = new ArrayList<>();
		for(String headPart : headParts)
			if(!headPart.equals("false"))
				headInstances.add(parseInstance(vocabulary, headPart));
		return new StatusClauseConverter().apply(bodyInstances, headInstances);
	}

	private static Instance parseInstance(Vocabulary voc, String part) {
		String[] split = part.substring(0, part.length() - 1).split("\\(");
		return voc.getInstance(split[0], SafeList.from(split[1].split(" ")).map(Integer::parseInt));
	}

	//endregion
}
