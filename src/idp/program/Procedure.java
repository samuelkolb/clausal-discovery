package idp.program;

import basic.StringUtil;
import vector.Vector;

import java.text.MessageFormat;
import java.util.Arrays;

/**
 * Created by samuelkolb on 11/11/14.
 *
 * @author Samuel Kolb
 */
public class Procedure {

	public static class MissingParametersException extends RuntimeException {
		private final String[] missing;

		public MissingParametersException(String[] missing) {
			this.missing = missing;
		}

		@Override
		public String getMessage() {
			return "No values given for the required parameters: " + StringUtil.join(", ", missing);
		}
	}

	//region Variables
	private final String program;

	private final Vector<String> parameters;

	private final Vector<Function> functions;
	//endregion

	//region Construction

	protected Procedure(String program, Vector<String> parameters, Vector<Function> functions) {
		this.program = program;
		this.parameters = parameters;
		this.functions = functions;
	}

	//endregion

	//region Public methods

	public String print(String... args) {
		if(args.length < parameters.length) {
			String[] missing = Arrays.copyOfRange(parameters.getArray(), args.length, parameters.length);
			throw new MissingParametersException(missing);
		}
		StringBuilder builder = new StringBuilder();
		builder.append("procedure main() {\n");
		for(Function function : functions)
			builder.append(function.print());
		builder.append(MessageFormat.format(program, args));
		builder.append("}\n");
		return builder.toString();
	}

	//endregion
}
