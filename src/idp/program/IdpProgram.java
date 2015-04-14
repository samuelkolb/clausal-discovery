package idp.program;

import idp.IdpProgramPrinter;
import logic.theory.LogicProgram;

import java.util.Optional;

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

	private final Optional<String> backgroundFile;

	public Optional<String> getBackgroundFile() {
		return backgroundFile;
	}

	//endregion

	//region Construction

	/**
	 * Creates an IDP program which containsInstance a LogicProgram
	 * @param program       	The LogicProgram
	 * @param backgroundFile	The optional name of the file containing background knowledge
	 */
	public IdpProgram(LogicProgram program, Optional<String> backgroundFile) {
		this.program = program;
		this.backgroundFile = backgroundFile;
	}

	//endregion

	//region Public methods

	protected void printProgram(StringBuilder builder) {
		builder.append(new IdpProgramPrinter().print(this.program));
		if(getBackgroundFile().isPresent())
			builder.append("include \"").append(getBackgroundFile().get()).append("\"\n\n");
	}

	/**
	 * Prints out the program
	 * @return	A string containing the printed version of the program
	 */
	public abstract String print();

	//endregion
}
