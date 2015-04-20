package logic.theory;

import java.io.File;

/**
 * Created by samuelkolb on 20/04/15.
 *
 * @author Samuel Kolb
 */
public class FileTheory implements Theory {

	//region Variables
	private final File file;

	public File getFile() {
		return file;
	}

	//endregion

	//region Construction

	/**
	 * Create a new file theory
	 * @param file	The file that contains the theory
	 */
	public FileTheory(File file) {
		this.file = file;
	}

	//endregion

	//region Public methods

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visit(this);
	}


	//endregion
}
