package idp;

import clausal_discovery.core.PredicateDefinition;
import logic.bias.Type;
import logic.expression.formula.Formula;
import logic.theory.*;
import map.DefaultMap;

import java.util.Map;

/**
 * The idp program printer prints a logic program (vocabulary, theory, structure) for idp
 *
 * @author Samuel Kolb
 */
public class IdpProgramPrinter {

	public static class Cached extends IdpProgramPrinter {

		private final Map<Structure, String> structures = new DefaultMap<>(this::printStructure, Structure.class,
				new DefaultMap.GenerationPolicy.Save());

		private String printStructure(Structure structure) {
			StringBuilder builder = new StringBuilder("{\n");
			for(Structure.TypeElement typeElement : structure.getTypeElements()) {
				if(typeElement.getType().getName().equals("int"))
					continue;
				builder.append("\t").append(typeElement.print()).append("\n");
			}
			for(Structure.PredicateElement predicateElement : structure.getPredicateElements())
				builder.append("\t").append(predicateElement.print()).append("\n");
			builder.append("}\n\n");
			return builder.toString();
		}

		@Override
		public String printStructure(Structure structure, String name, String vocabularyName) {
			return "structure " + name + ":" + vocabularyName + structures.get(structure);
		}
	}

	private class TheoryVisitor implements Theory.Visitor<String> {

		private final String theoryName;
		private final String vocabularyName;

		private TheoryVisitor(String theoryName, String vocabularyName) {
			this.theoryName = theoryName;
			this.vocabularyName = vocabularyName;
		}

		@Override
		public String visit(InlineTheory inlineTheory) {
			StringBuilder builder = new StringBuilder();
			builder.append("theory ").append(theoryName).append(":").append(vocabularyName).append(" {\n");
			for(Formula formula : inlineTheory.getFormulas()) {
				String string = IdpExpressionPrinter.print(formula);
				for(String line : string.split("\n"))
					builder.append('\t').append(line);
				builder.append(".\n");
			}
			builder.append("}\n\n");
			return builder.toString();
		}

		@Override
		public String visit(FileTheory fileTheory) {
			return "include \"" + fileTheory.getFile().getAbsolutePath() + "\"\n\n";
		}
	}

	public static final String VOCABULARY_NAME = "V";
	public static final String THEORY_PREFIX = "T";
	public static final String BACKGROUND_PREFIX = "B";
	public static final String STRUCTURE_PREFIX = "S";

	public String print(KnowledgeBase program) {
		return printVocabulary(program.getVocabulary(), VOCABULARY_NAME)
				+ printTheories(program, THEORY_PREFIX, BACKGROUND_PREFIX, VOCABULARY_NAME)
				+ printStructures(program, STRUCTURE_PREFIX, VOCABULARY_NAME);
	}

	public String printTheory(Theory theory, String name, String vocabularyName) {
		return theory.accept(new TheoryVisitor(name, vocabularyName));
	}

	public String printVocabulary(Vocabulary vocabulary, String name) {
		StringBuilder builder = new StringBuilder();
		builder.append("vocabulary ").append(name).append(" {\n");
		for(Type type : vocabulary.getTypes()) {
			if(type.isBuiltIn())
				continue;
			builder.append("\ttype ").append(type.getName());
			if(type.hasParent())
				builder.append(" isa ").append(type.getParent().getName());
			builder.append('\n');
		}
		for(PredicateDefinition definition : vocabulary.getDefinitions())
			builder.append("\t").append(definition.getPredicate()).append("\n");
		builder.append("}\n\n");
		return builder.toString();
	}

	public String printStructure(Structure structure, String name, String vocabularyName) {
		StringBuilder builder = new StringBuilder();
		builder.append("structure ").append(name).append(":").append(vocabularyName).append(" {\n");
		for(Structure.TypeElement typeElement : structure.getTypeElements()) {
			if(typeElement.getType().getName().equals("int"))
				continue;
			builder.append("\t").append(typeElement.print()).append("\n");
		}
		for(Structure.PredicateElement predicateElement : structure.getPredicateElements())
			builder.append("\t").append(predicateElement.print()).append("\n");
		builder.append("}\n\n");
		return builder.toString();
	}

	public String printTheories(KnowledgeBase program, String prefix, String backgroundPrefix,  String vocabularyName) {
		if(program.getTheories().isEmpty())
			return "";
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < program.getBackgroundTheories().size(); i++)
			builder.append(printTheory(program.getBackgroundTheories().get(i), backgroundPrefix + i, vocabularyName));
		for(int i = 0; i < program.getTheories().size(); i++)
			builder.append(printTheory(program.getTheories().get(i), prefix + i, vocabularyName));
		return builder.toString();
	}

	public String printStructures(KnowledgeBase program, String prefix, String vocabularyName) {
		if(program.getStructures().isEmpty())
			return "";
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < program.getStructures().size(); i++)
			builder.append(printStructure(program.getStructures().get(i), prefix + i, vocabularyName));
		return builder.toString();
	}
}
