package util;

import log.Log;
import runtime.Terminal;

import java.io.*;

/**
 * Created by samuelkolb on 21/04/15.
 *
 * @author Samuel Kolb
 */
public class TemporaryFile {

	private class Writer implements Runnable {

		private final String content;

		private Writer(String content) {
			this.content = content;
		}

		@Override
		public void run() {
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(new BufferedWriter(new FileWriter(getFile())));
				writer.write(this.content);
			} catch(IOException e) {
				throw new IllegalStateException("Unexpected error.", e);
			} finally {
				if(writer != null)
					writer.close();
			}
		}
	}

	//region Variables

	private final File file;

	public File getFile() {
		return file;
	}

	private final Thread thread;

	//endregion

	//region Construction

	/**
	 * Creates a new temporary file
	 * @param file		The file to wrap
	 * @param content	The content to write
	 */
	public TemporaryFile(File file, String content) {
		this.file = file;
		//Terminal.get().execute("mkfifo " + getFile().getAbsolutePath(), true);
		this.thread = new Thread(new Writer(content));
		this.thread.start();
	}


	//endregion

	//region Public methods

	/**
	 * Returns whether this temporary file is still being used
	 * @return	True iff this file has been created and is being written to
	 */
	public boolean isAlive() {
		return this.thread.isAlive();
	}

	/**
	 * Waits for the writing thread to finish and then deletes the temporary file
	 */
	public void delete() {
		try {
			this.thread.join();
		} catch(InterruptedException e) {
			throw new IllegalStateException("Interrupted while waiting for writing thread to finish");
		}
		Terminal.get().execute("unlink " + getFile().getAbsolutePath(), true);
	}

	/*
	 * 	private File createFile(final String string) {
		final File file = getFileManager().createRandomFile("idp");
		getTerminal().execute("mkfifo " + file.getAbsolutePath(), true);
		new Thread(() -> {
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				writer.write(string);
			} catch(IOException e) {
				throw new IllegalStateException("Unexpected error.", e);
			} finally {
				if(writer != null)
					writer.close();
			}
		}).start();
		return file;
	}

	 */
	//endregion
}
