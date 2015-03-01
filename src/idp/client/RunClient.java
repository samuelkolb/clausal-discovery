package idp.client;

import basic.FileUtil;
import basic.StringUtil;
import log.Log;
import idp.IdpExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by samuelkolb on 20/11/14.
 *
 * @author Samuel Kolb
 */
public class RunClient {

	//region Variables

	//endregion

	//region Construction

	//endregion

	//region Public methods
	public static void main(String[] args) {
		String filename;
		if(args.length >= 1)
			filename = args[0];
		else {
			Log.LOG.printLine("Enter filename to load (in idp_programs directory)");
			filename = new Scanner(System.in).nextLine();
		}
		File clientFile = FileUtil.getLocalFile(RunClient.class.getResource("/idp_programs/" + filename));
		String output;
		try {
			output = IdpExecutor.get().execute(clientFile);
			Log.LOG.printTitle("Client output");
		} catch(IllegalArgumentException e) {
			Log.LOG.printTitle("An error occurred");
			output = formatError(e.getMessage(), true);
		}
		Log.LOG.printLine(output);
	}

	public static String formatError(String output, boolean ignoreOutput) {
		String[] lines = output.split("\n");
		List<String> result = new ArrayList<>();
		List<String> warnings = new ArrayList<>();
		for(String line : lines)
			if(line.startsWith("Warning:"))
				warnings.add(formatLine(line.substring(9)));
			else if(line.startsWith("Error:"))
				result.add(formatLine(line.substring(7)));
		return (ignoreOutput ? "" : "WARNINGS:\n" + getJoined(warnings) + "\n\n") + "ERRORS:\n" + getJoined(result);
	}

	private static String formatLine(String line) {
		String[] parts = line.split(" At ");
		return parts.length == 2 ? "[" + parts[1].substring(parts[1].lastIndexOf("/") + 1) + "] " + parts[0] : line;
	}

	private static String getJoined(List<String> list) {
		return StringUtil.join("\n", list.toArray(new String[list.size()]));
	}
	//endregion
}
