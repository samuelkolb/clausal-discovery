package idp.program;

import idp.IdpProgramPrinter;
import logic.theory.KnowledgeBase;

/**
 * Created by samuelkolb on 11/11/14.
 *
 * @author Samuel Kolb
 */
public abstract class IdpProgram {

	//region Variables
	private final KnowledgeBase knowledgeBase;

	public KnowledgeBase getKnowledgeBase() {
		return knowledgeBase;
	}

	private IdpProgramPrinter printer = new IdpProgramPrinter();

	public IdpProgramPrinter getPrinter() {
		return printer;
	}

	public void setPrinter(IdpProgramPrinter printer) {
		this.printer = printer;
	}

	//endregion

	//region Construction

	/**
	 * Creates an IDP knowledgeBase which containsInstance a LogicProgram
	 * @param knowledgeBase       	The LogicProgram
	 */
	public IdpProgram(KnowledgeBase knowledgeBase) {
		this.knowledgeBase = knowledgeBase;
	}

	//endregion

	//region Public methods

	protected void printProgram(StringBuilder builder) {
		builder.append(new IdpProgramPrinter().print(getKnowledgeBase()));
	}

	protected String mergeBackground(String theory) {
		for(int i = 0; i < getKnowledgeBase().getBackgroundTheories().size(); i++)
			theory = merge(theory, "B" + i);
		return theory;
	}

	protected String merge(String theory1, String theory2) {
		return String.format("merge(%s, %s)", theory1, theory2);
	}

	/**
	 * Prints out the knowledgeBase
	 * @return	A string containing the printed version of the knowledgeBase
	 */
	public abstract String print();

	//endregion
}
