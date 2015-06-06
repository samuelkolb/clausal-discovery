package idp;

import basic.FileUtil;
import runtime.Terminal;
import util.Path;

import java.io.File;

/**
 * Created by samuelkolb on 06/06/15.
 *
 * @author Samuel Kolb
 */
public class IDP {

	/**
	 * Execute the given input
	 * @param input	The input (via std-in)
	 * @return	The IDP output
	 */
	public static String execute(String input) {
		return Terminal.get().runCommand(Path.IDP.getFullPath(), input);
	}

	/**
	 * Execute the given file
	 * @param file	The file
	 * @return	The IDP output
	 */
	public static String execute(File file) {
		String filePath = file.getAbsolutePath();
		return Terminal.get().runCommand(String.format("%s %s", Path.IDP.getFullPath(), filePath));
	}
}
