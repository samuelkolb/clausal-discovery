package idp;

import logic.bias.Type;
import logic.expression.formula.Formula;
import logic.expression.formula.Predicate;
import logic.theory.*;

/**
 * The idp program printer prints a logic program (vocabulary, theory, structure) for idp
 *
 * @author Samuel Kolb
 */
public class IdpProgramPrinter extends ProgramPrinter {

	@Override
	public String print(LogicProgram program) {
		// TODO find a solution for the names
		String vocabularyName = "V";
		String theoryName = "T";
		String structureName = "S";
		return printVocabulary(program.getVocabulary(), vocabularyName)
				+ printTheory(program.getTheory(), theoryName, vocabularyName)
				+ printStructures(program, structureName, vocabularyName);
	}

	@Override
	public String printTheory(Theory theory, String name, String vocabularyName) {
		StringBuilder builder = new StringBuilder();
		builder.append("theory ").append(name).append(":").append(vocabularyName).append(" {\n");
		for(Formula formula : theory.getFormulas()) {
			String string = IdpExpressionPrinter.print(formula);
			for(String line : string.split("\n"))
				builder.append('\t').append(line);
			builder.append(".\n");
		}
		builder.append("}\n\n");
		return builder.toString();
	}

	@Override
	public String printVocabulary(Vocabulary vocabulary, String name) {
		StringBuilder builder = new StringBuilder();
		builder.append("vocabulary ").append(name).append(" {\n");
		for(Type type : vocabulary.getTypes())
			builder.append("\ttype ").append(type.getName()).append('\n');
		for(Predicate predicate : vocabulary.getPredicates())
			builder.append("\t").append(predicate).append("\n");
		builder.append("}\n\n");
		return builder.toString();
	}

	@Override
	public String printStructure(Structure structure, String name, String vocabularyName) {
		StringBuilder builder = new StringBuilder();
		builder.append("structure ").append(name).append(":").append(vocabularyName).append(" {\n");
		for(Structure.TypeElement typeElement : structure.getTypeElements())
			builder.append("\t").append(typeElement.print()).append("\n");
		for(Structure.PredicateElement predicateElement : structure.getPredicateElements())
			builder.append("\t").append(predicateElement.print()).append("\n");
		builder.append("}\n\n");
		return builder.toString();
	}
}
