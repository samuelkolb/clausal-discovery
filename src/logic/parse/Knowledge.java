package logic.parse;

import basic.StringUtil;
import vector.Vector;
import clausal_discovery.LogicBase;
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

	public Knowledge(Vocabulary vocabulary, Vector<Example> examples) {
		this.vocabulary = vocabulary;
		this.examples = examples;
	}

	@Override
	public String toString() {
		return new IdpProgramPrinter().printVocabulary(getVocabulary(), "Vocabulary")
				+ StringUtil.join("\n", examples.getArray());
	}
}
