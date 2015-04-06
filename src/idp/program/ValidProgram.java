package idp.program;

import vector.Vector;
import logic.theory.LogicProgram;

import java.util.Optional;

/**
 * Represents a program that tests whether the given theory is valid on the structures in the logic program
 *
 * @author Samuel Kolb
 */
public class ValidProgram extends IdpProgram {

	/**
	 * Constructs a new valid program
	 * @param program			The logic program with the theory and structures
	 * @param backgroundFile	The optional name of the file containing background knowledge
	 */
	public ValidProgram(LogicProgram program, Optional<String> backgroundFile) {
		super(program, backgroundFile);
	}

	@Override
	public String print() {
		StringBuilder builder = new StringBuilder();
		printProgram(builder);

		StringBuilder procedure = new StringBuilder();
		for(int i = 0; i < getProgram().getTheories().size(); i++) {
			if(getBackgroundFile().isPresent())
				procedure.append("t").append(i).append(" = merge(B, T").append(i).append(")\n");
			else
				procedure.append("t").append(i).append(" = T").append(i).append("\n");
			queryTheory("t" + i, procedure);
		}
		procedure.append("print(\"\")\n");

		Vector<Function> functions = new Vector<Function>(Functions.VALID.getFunction());
		builder.append(new Procedure(procedure.toString(), new Vector<>(), functions).print());
		return builder.toString();
	}

	private void queryTheory(String theoryName, StringBuilder procedure) {
		procedure.append("if true");
		for(int i = 0; i < getProgram().getStructures().size(); i++)
			procedure.append(" and isValid(").append(theoryName).append(", S").append(i).append(")");
		procedure.append(" then\n\tprint(\"YES\")\nelse\n\tprint(\"NO\")\nend\n");
	}
}
