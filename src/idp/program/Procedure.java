package idp.program;

import basic.StringUtil;

import java.text.MessageFormat;
import java.util.Arrays;

/**
 * Created by samuelkolb on 11/11/14.
 *
 * @author Samuel Kolb
 */
public abstract class Procedure {

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
	private final String name;

	private final String[] parameters;
	//endregion

	//region Construction

	public Procedure(String name, String... parameters) {
		this.name = name;
		this.parameters = parameters;
	}

	//endregion

	//region Public methods

	public abstract String getContent();

	public String print(String... args) {
		if(args.length < parameters.length)
			throw new MissingParametersException(Arrays.copyOfRange(parameters, args.length, parameters.length));
		return "procedure " + name + "() {\n" + MessageFormat.format(getContent(), args) + "}\n";
	}

	//endregion
}
