package idp;

import basic.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;

/**
 * Created by samuelkolb on 11/11/14.
 *
 * @author Samuel Kolb
 */
public class FileManager {

	//region Variables
	private static final char[] CHARACTERS = new char[]{'0','1','2','3','4','5','6','7','8','9',
			'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};

	private static final int FILENAME_LENGTH = 48;

	// IVAR tempDir - Directory to store temporary files

	protected final File tempDir;

	public File getTempDir() {
		return tempDir;
	}

	// IVAR properties - Properties file

	private final Properties properties;

	protected Properties getProperties() {
		return properties;
	}

	// IVAR random - The random generator used for random file names

	private final Random random = new Random(System.currentTimeMillis());

	protected Random getRandom() {
		return random;
	}

	//endregion

	//region Construction

	/**
	 * Creates a new file manager
	 * Initiates temp properties
	 * @param folder			The folder name in the resources folder
	 */
	public FileManager(String folder) {
		this.properties = new Properties();
		String fileName = "config.properties";
		try {
			File dir = FileUtil.getLocalFile(this.getClass().getResource("/" + folder));
			if(dir == null)
				throw new IllegalStateException();
			this.tempDir = dir;
		} catch(IllegalArgumentException e) {
			throw new IllegalStateException("Missing directory: /" + folder, e);
		}
		try {
			File propertiesFile = new File(getTempDir(), fileName);
			getProperties().load(new FileInputStream(propertiesFile));
		} catch(IllegalArgumentException e) {
			throw new IllegalStateException("Missing file: /" + folder + "/" + fileName, e);
		} catch(Exception e) {
			throw new IllegalStateException("Unexpected error", e);
		}
	}

	//endregion

	//region Public methods

	/**
	 * Creates a temporary file with the given extension
	 * @param extension	The extension of the temporary file to be created (e.g. txt)
	 * @return	A file object (that has not yet been created)
	 */
	public File createRandomFile(String extension) {
		Objects.requireNonNull(extension);
		File file;
		do {
			file = new File(getTempDir(), getRandomString() + "." + extension);
		} while(file.exists());
		return file;
	}

	/**
	 * Deletes all files within the directory that have the given extension
	 * @param extension	The extension of the files to be deleted
	 */
	public void cleanTempDir(String extension) {
		Objects.requireNonNull(extension);
		File[] files = getTempDir().listFiles();
		if(files == null)
			throw new IllegalStateException("Could not list temp directory");
		for(File file : files) {
			if(file == null)
				continue;
			if(extension.equals(FileUtil.getExtension(file)))
				file.delete();
		}

	}
	//endregion

	// region Private methods

	private String getRandomString() {
		char[] array = new char[FILENAME_LENGTH];
		for(int i = 0; i < FILENAME_LENGTH; i++)
			array[i] = CHARACTERS[getRandom().nextInt(CHARACTERS.length)];
		return new String(array);
	}

	// endregion
}
