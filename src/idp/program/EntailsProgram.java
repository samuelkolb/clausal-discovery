package idp.program;

import idp.IdpProgramPrinter;
import logic.theory.InlineTheory;
import logic.theory.LogicProgram;
import vector.Vector;

import java.util.Optional;

/**
 * Represents a program that checks whether a theory entails a given clause
 *
 * @author Samuel Kolb
 */
public class EntailsProgram extends IdpProgram {

	//region Variables
	private static final Procedure ENTAIL_PROCEDURE = Procedures.ENTAILS.getProcedure();

	private final InlineTheory theory;
	//endregion

	//region Construction

	/**
	 * Constructs a new entails program
	 * @param program	The logic program containing vocabulary and theory
	 * @param theory	The theory that has to be checked
	 * @param backgroundFile	The optional name of the file containing background knowledge
	 */
	public EntailsProgram(LogicProgram program, InlineTheory theory, Optional<String> backgroundFile) {
		super(program, backgroundFile);
		this.theory = theory;
	}

	//endregion

	//region Public methods

	@Override
	public String print() {
		StringBuilder builder = new StringBuilder();
		printProgram(builder);
		builder.append(new IdpProgramPrinter().printTheory(theory, "T1", "V"));
		StringBuilder procedure = new StringBuilder();
		// TODO Check if valid
		if(getBackgroundFile().isPresent())
			/*procedure.append("t0 = merge(T0, B)\nt1 = T1\n");
			/*/procedure.append("t0 = merge(T0, B)\nt1 = merge(T1, B)\n");/**/
		else
			procedure.append("t0 = T0\nt1 = T1\n");
		procedure.append(ENTAIL_PROCEDURE.printProgram("t0", "t1"));
		builder.append(new Procedure(procedure.toString(), new Vector<>(), new Vector<>()).print());
		return builder.toString();
	}

	//endregion
}
