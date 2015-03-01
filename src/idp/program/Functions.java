package idp.program;

import basic.FileUtil;

/**
 * Created by samuelkolb on 01/03/15.
 */
public enum Functions {

	VALID("valid.txt");

	private final String program;

	Functions(String filename) {
		program = FileUtil.readFile(FileUtil.getLocalFile(getClass().getResource("/idp_programs/" + filename)));
	}

	public Function getFunction() {
		return new Function(program);
	}

}
