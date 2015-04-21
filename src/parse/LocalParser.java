package parse;

import basic.FileUtil;

import java.io.File;

/**
 * Created by samuelkolb on 21/04/15.
 *
 * @author Samuel Kolb
 */
public interface LocalParser<R> {

	/**
	 * Read and parse the local file with the given name. (Local files reside under res/examples/)
	 * @param name	The name of the file (including the extension)
	 * @return	The result parsed from the file
	 */
	public default R parseLocalFile(String name) {
		File file = FileUtil.getLocalFile(getClass().getResource("/examples/" + name));
		return parse(FileUtil.readFile(file));
	}

	/**
	 * Parse the given content to obtain a result
	 * @param content	The string to parse
	 * @return	The result
	 */
	public R parse(String content);
}
