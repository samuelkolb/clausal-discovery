package idp.program;

import basic.StringUtil;
import vector.SafeList;

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

	private final SafeList<String> parameters;

	private final SafeList<Function> functions;
	//endregion

	//region Construction

	protected Procedure(String program) {
		this(program, new SafeList<>(), new SafeList<>());
	}

	protected Procedure(String program, SafeList<String> parameters, SafeList<Function> functions) {
		this.program = program;
		this.parameters = parameters;
		this.functions = functions;
	}

	//endregion

	//region Public methods

	public String print(String... args) {
		return "procedure main() {\n" + printProgram(args) + "}\n";
	}

	public String printProgram(String... args) {
		if(args.length < parameters.size()) {
			String[] missing = Arrays.copyOfRange(parameters.toArray(new String[parameters.size()]), args.length, parameters.size());
			throw new MissingParametersException(missing);
		}
		StringBuilder builder = new StringBuilder();
		for(Function function : functions)
			builder.append(function.print());
		builder.append(MessageFormat.format(program, args));
		return builder.toString();
	}

	//endregion
}
