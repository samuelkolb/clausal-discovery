package idp.program;

import logic.theory.KnowledgeBase;

/**
 * Created by samuelkolb on 29/04/15.
 *
 * @author Samuel Kolb
 */
public class ValidityProgram extends ValidProgram {

	//region Variables

	//endregion

	//region Construction

	public ValidityProgram(KnowledgeBase knowledgeBase) {
		super(knowledgeBase);
	}

	//endregion

	//region Public methods

	@Override
	protected void queryTheory(String theoryName, StringBuilder procedure) {
		for(int i = 0; i < getKnowledgeBase().getStructures().size(); i++)
			procedure.append(String.format("if isValid(%s, S%d) then io.write(\"YES \") else io.write(\"NO \") end\n",
					theoryName, i));
		procedure.append("io.write(\"\\n\")");
	}

	//endregion
}
