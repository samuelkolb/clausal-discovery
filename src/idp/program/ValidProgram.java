package idp.program;

import basic.FileUtil;
import vector.Vector;
import logic.theory.LogicProgram;

/**
 * Represents a program that tests whether the given theory is valid on the structures in the logic program
 *
 * @author Samuel Kolb
 */
public class ValidProgram extends IdpProgram {

	/**
	 * Constructs a new valid program
	 * @param program	The logic program with the theory and structures
	 */
	public ValidProgram(LogicProgram program) {
		super(program);
	}

	@Override
	public String print() {
		StringBuilder builder = new StringBuilder();
		printProgram(builder);

		StringBuilder procedure = new StringBuilder();
		procedure.append(FileUtil.readFile(FileUtil.getLocalFile(getClass().getResource("/idp_programs/valid.txt"))));
		procedure.append("\nif true");
		for(int i = 0; i < getProgram().getStructures().size(); i++) {
			procedure.append(" and isValid(T, S").append(i).append(")");
		}
		procedure.append(" then\n\tprint(\"YES\")\nelse\n\tprint(\"NO\")\nend");
		builder.append(new LinesProcedure("main", new Vector<>(procedure.toString().split("\n"))).print());
		return builder.toString();
	}
}
