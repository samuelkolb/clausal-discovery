package idp.program;

import basic.FileUtil;
import vector.SafeList;

/**
 * Created by samuelkolb on 11/11/14.
 *
 * @author Samuel Kolb
 */
public enum Procedures {

	// 0: Theory, 1: Theory that might be entailed
	ENTAILS("entails.txt", new SafeList<>("Entailing theory", "Entailed theory"), new SafeList<Function>());

	private final String program;

	private final SafeList<String> parameters;

	private final SafeList<Function> functions;

	Procedures(String filename, SafeList<String> parameters, SafeList<Function> functions) {
		this.parameters = parameters;
		this.functions = functions;
		program = FileUtil.readFile(FileUtil.getLocalFile(getClass().getResource("/idp_programs/" + filename)));
	}

	/**
	 * Returns the procedure
	 * @return	A procedure containing the code to perform the operation
	 */
	public Procedure getProcedure() {
		return new Procedure(program, parameters, functions);
	}
}
