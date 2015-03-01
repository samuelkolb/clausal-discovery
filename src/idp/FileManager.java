package idp;

import basic.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Random;

/**
 * Created by samuelkolb on 11/11/14.
 *
 * @author Samuel Kolb
 */
public class FileManager {

	//region Variables
	public static final FileManager instance = new FileManager();

	private final File tempDir;

	public File getTempDir() {
		return tempDir;
	}

	private final Properties properties;

	private static final char[] CHARACTERS = new char[]{'0','1','2','3','4','5','6','7','8','9',
			'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	private static Random random = new Random(System.currentTimeMillis());

	private static final int FILENAME_LENGTH = 48;

	//endregion

	//region Construction

	/**
	 * Creates a new file manager
	 * Initiates temp properties
	 */
	private FileManager() {
		this.properties = new Properties();
		String fileName = "temp.properties";
		try {
			File dir = FileUtil.getLocalFile(this.getClass().getResource("/temp"));
			if(dir == null)
				throw new IllegalStateException();
			this.tempDir = dir;
		} catch(IllegalArgumentException e) {
			throw new IllegalStateException("Missing directory: /temp", e);
		}
		cleanTempDir();
		try {
			File propertiesFile = new File(getTempDir(), fileName);
			properties.load(new FileInputStream(propertiesFile));
		} catch(IllegalArgumentException e) {
			throw new IllegalStateException("Missing file: /temp/" + fileName, e);
		} catch(Exception e) {
			throw new IllegalStateException("Unexpected error", e);
		}
	}

	//endregion

	//region Public methods

	public File createTempFile(String extension) {
		File file;
		do {
			file = new File(getTempDir(), getRandomString() + "." + extension);
		} while(file.exists());
		return file;
	}

	private String getRandomString() {
		char[] array = new char[FILENAME_LENGTH];
		for(int i = 0; i < FILENAME_LENGTH; i++)
			array[i] = CHARACTERS[random.nextInt(CHARACTERS.length)];
		return new String(array);
	}

	//endregion

	private void cleanTempDir() {
		File[] files = getTempDir().listFiles();
		if(files == null)
			throw new IllegalStateException("Could not list temp directory");
		for(File file : files) {
			if(file == null)
				continue;
			if("idp".equals(FileUtil.getExtension(file)))
				file.delete();
		}

	}
}
