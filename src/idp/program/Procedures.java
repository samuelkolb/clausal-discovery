package idp.program;

import basic.FileUtil;
import vector.Vector;

/**
 * Created by samuelkolb on 11/11/14.
 *
 * @author Samuel Kolb
 */
public enum Procedures {

	// 0: Theory, 1: Theory that might be entailed
	ENTAILS("entails.txt", new Vector<String>("Entailing theory", "Entailed theory"), new Vector<Function>());

	private final String program;

	private final Vector<String> parameters;

	private final Vector<Function> functions;

	Procedures(String filename, Vector<String> parameters, Vector<Function> functions) {
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
