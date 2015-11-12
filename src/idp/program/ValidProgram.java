package idp.program;

import idp.IdpProgramPrinter;
import logic.theory.KnowledgeBase;
import vector.Vector;

/**
 * Represents a program that tests whether the given theory is valid on the structures in the logic program
 *
 * @author Samuel Kolb
 */
public class ValidProgram extends IdpProgram {

	/**
	 * Constructs a new valid program
	 * @param knowledgeBase		The logic program with the theory and structures
	 * @param printer			The idp program printer
	 */
	public ValidProgram(KnowledgeBase knowledgeBase, IdpProgramPrinter printer) {
		super(knowledgeBase, printer);
	}

	@Override
	public String print() {
		StringBuilder builder = new StringBuilder();
		printProgram(builder);

		StringBuilder procedure = new StringBuilder();
		for(int i = 0; i < getKnowledgeBase().getTheories().size(); i++) {
			procedure.append("t").append(i).append(" = ").append(mergeBackground("T" + i)).append("\n");
			queryTheory("t" + i, procedure);
		}
		procedure.append("print(\"\")\n");

		Vector<Function> functions = new Vector<>(Functions.VALID.getFunction());
		builder.append(new Procedure(procedure.toString(), new Vector<>(), functions).print());
		return builder.toString();
	}

	protected void queryTheory(String theoryName, StringBuilder procedure) {
		procedure.append("if true");
		for(int i = 0; i < getKnowledgeBase().getStructures().size(); i++)
			procedure.append(" and isValid(").append(theoryName).append(", S").append(i).append(")");
		procedure.append(" then\n\tprint(\"YES\")\nelse\n\tprint(\"NO\")\nend\n");
	}
}
