package idp.program;

import idp.IdpProgramPrinter;
import logic.theory.LogicProgram;
import logic.theory.Theory;

/**
 * Represents a program that checks whether a theory entails a given clause
 *
 * @author Samuel Kolb
 */
public class EntailsProgram extends IdpProgram {

	//region Variables
	private static final Procedure ENTAIL_PROCEDURE = Procedures.ENTAILS.getProcedure();

	private final Theory theory;
	//endregion

	//region Construction

	/**
	 * Constructs a new entails program
	 * @param program	The logic program containing vocabulary and theory
	 * @param theory	The theory that has to be checked
	 */
	public EntailsProgram(LogicProgram program, Theory theory) {
		super(program);
		this.theory = theory;
	}

	//endregion

	//region Public methods

	@Override
	public String print() {
		StringBuilder builder = new StringBuilder();
		printProgram(builder);
		builder.append(new IdpProgramPrinter().printTheory(theory, "T2", "V"));
		builder.append(ENTAIL_PROCEDURE.print("T", "T2"));
		return builder.toString();
	}

	//endregion
}
