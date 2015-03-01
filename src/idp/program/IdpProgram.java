package idp.program;

import idp.IdpProgramPrinter;
import logic.theory.LogicProgram;

/**
 * Created by samuelkolb on 11/11/14.
 *
 * @author Samuel Kolb
 */
public abstract class IdpProgram {

	//region Variables
	private final LogicProgram program;

	public LogicProgram getProgram() {
		return program;
	}

	//endregion

	//region Construction

	/**
	 * Creates an IDP program which contains a LogicProgram
	 * @param program		The LogicProgram
	 */
	public IdpProgram(LogicProgram program) {
		this.program = program;
	}

	//endregion

	//region Public methods

	protected void printProgram(StringBuilder builder) {
		builder.append(new IdpProgramPrinter().print(this.program));
	}

	public abstract String print();

	//endregion
}
