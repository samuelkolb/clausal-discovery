package idp.program;

import idp.IdpProgramPrinter;
import logic.theory.KnowledgeBase;
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
	public EntailsProgram(KnowledgeBase program, Theory theory) {
		super(program);
		if(getKnowledgeBase().getTheories().size() != 1)
			throw new IllegalArgumentException("Knowledge base should contains exactly one theory");
		this.theory = theory;
	}

	//endregion

	//region Public methods

	@Override
	public String print() {
		StringBuilder builder = new StringBuilder();
		printProgram(builder);
		builder.append(new IdpProgramPrinter().printTheory(theory, "T1", "V"));
		String program = String.format("t0 = %s\n", mergeBackground("T0"))
				+ String.format("t1 = %s\n", "T1")
				+ ENTAIL_PROCEDURE.printProgram("t0", "t1");
		builder.append(new Procedure(program).print());
		return builder.toString();
	}

	//endregion
}
