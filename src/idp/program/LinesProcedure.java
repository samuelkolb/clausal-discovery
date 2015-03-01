package idp.program;

import vector.Vector;

/**
 * Created by samuelkolb on 18/11/14.
 */
public class LinesProcedure extends Procedure {

	private final Vector<String> code;

	public LinesProcedure(String name, Vector<String> code, String... parameters) {
		super(name, parameters);
		this.code = code;
	}

	@Override
	public String getContent() {
		StringBuilder builder = new StringBuilder();
		for(String line : code)
			builder.append("\t").append(line).append("\n");
		return builder.toString();
	}
}
