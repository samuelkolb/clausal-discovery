package idp.program;

import basic.FileUtil;
import vector.Vector;

/**
 * Created by samuelkolb on 11/11/14.
 *
 * @author Samuel Kolb
 */
public enum Procedures {

	// 0: Theory, 1: Structure
	VALID("valid.txt"),

	// 0: Theory, 1: Theory that might be entailed
	ENTAILS("entails.txt");

	private final String[] program;

	Procedures(String filename) {
		String content = FileUtil.readFile(FileUtil.getLocalFile(getClass().getResource("/idp_programs/" + filename)));
		program = content.split("\n");
	}

	/**
	 * Returns the procedure
	 * @param name	The name the procedure should have
	 * @return	A procedure containing the code to perform the operation
	 */
	public Procedure getProcedure(String name) {
		Vector<String> result = new Vector<>(program);
		return new LinesProcedure(name, result);
	}
}
