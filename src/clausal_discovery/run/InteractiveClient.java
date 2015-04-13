package clausal_discovery.run;

import basic.FileUtil;
import log.Log;

import java.io.File;
import java.util.Scanner;

/**
 * Created by samuelkolb on 13/04/15.
 *
 * @author Samuel Kolb
 */
public class InteractiveClient {

	/**
	 * Runs the interactive client
	 * @param args	Command line arguments
	 */
	public static void main(String[] args) {
		String name;
		Scanner scanner = new Scanner(System.in);
		if(args.length > 0)
			name = args[0];
		else {
			String[] examples = getExamples();
			Log.LOG.printLine("Choose example:");
			for(int i = 0; i < examples.length; i++)
				Log.LOG.printLine("[" + i + "] " + examples[i]);
			name = examples[scanner.nextInt()];
			name = name.substring(0, name.length() - 6);
		}
		Log.LOG.printLine("\nHow many variables can be used?");
		int variableCount = Integer.parseInt(args.length > 1 ? args[1] : scanner.nextLine());

		Log.LOG.printLine("\nWhat is the maximal clause length?");
		int clauseLength = Integer.parseInt(args.length > 2 ? args[2] : scanner.nextLine());

		new RunClient().run(new Configuration.FullFileConfiguration(name, variableCount, clauseLength));
	}

	private static String[] getExamples() {
		File folder = FileUtil.getLocalFile(InteractiveClient.class.getResource("/examples/"));
		return folder.list((dir, name) -> name.matches(".*\\.logic"));
	}
}
