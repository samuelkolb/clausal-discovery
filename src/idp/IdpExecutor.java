package idp;

import basic.FileUtil;
import basic.StringUtil;
import log.Log;
import runtime.Terminal;
import idp.program.EntailsProgram;
import idp.program.IdpProgram;
import idp.program.ValidProgram;
import logic.theory.LogicExecutor;
import logic.theory.LogicProgram;
import logic.theory.Theory;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by samuelkolb on 11/11/14.
 *
 * @author Samuel Kolb
 */
public class IdpExecutor implements LogicExecutor {

	//region Variables
	private static IdpExecutor executor;

	/**
	 * Returns the singleton executor instance
	 * @return	An idp executor
	 */
	public static IdpExecutor get() {
		if(executor == null)
			executor = new IdpExecutor();
		return executor;
	}

	private Terminal terminal = new Terminal();
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	public Terminal getTerminal() {
		return terminal;
	}

	//endregion

	//region Construction
	private IdpExecutor() {}
	//endregion

	//region Public methods

	@Override
	public void shutdown() {
		executorService.shutdown();
	}

	@Override
	public boolean isValid(LogicProgram program) {
		return executeTest(new ValidProgram(program));
	}

	@Override
	public boolean entails(LogicProgram program, Theory theory) {
		return executeTest(new EntailsProgram(program, theory));
	}

	private boolean executeTest(IdpProgram idpProgram) throws IllegalStateException {
		String string = executeSafe(idpProgram);
		if("YES".equals(string))
			return true;
		if("NO".equals(string))
			return false;
		throw new IllegalStateException("Incorrect output: " + string);
	}

	private String executeSafe(IdpProgram idpProgram) throws IllegalStateException {
		try {
			return execute(idpProgram);
		} catch(IllegalArgumentException e) {
			Log.LOG.printTitle("Error occurred:").printLine(e.getMessage()).newLine();
			Log.LOG.printTitle("Program:").printLine(getDebugString(idpProgram));
			throw new IllegalStateException(e);
		}
	}

	private String getDebugString(IdpProgram idpProgram) {
		String string = idpProgram.print();
		String[] lines = string.split("\n");
		if(lines.length == 0)
			return "";
		int digits = (int) Math.floor(Math.log10(lines.length)) + 1;
		for(int i = 0; i < lines.length; i++)
			lines[i] = printFixed(i+1, digits) + "| " + lines[i];
		return StringUtil.join("\n", (Object[]) lines);
	}

	private String printFixed(int number, int length) {
		int digits = (int) Math.floor(Math.log10(number) + 1);
		return StringUtil.getRepeated(' ', length - digits) + number;
	}

	/**
	 * Executes an idp program and returns the result
	 * @param program	The program to run
	 * @return	The output of idp
	 */
	public String execute(IdpProgram program) {
		return execute(program.print());
	}

	/**
	 * Executes an idp program and returns the result
	 * @param idpProgram	The name of the idp program file
	 * @return	The output of idp
	 */
	public String execute(String idpProgram) {
		File idpFile = createFile(idpProgram);
		return execute(idpFile);
	}

	/**
	 * Executes an idp program and returns the result
	 * @param file	The file containing an idp program
	 * @return	The output of idp
	 */
	public String execute(File file) {
		String idpPath = FileUtil.getLocalFile(getClass().getResource("/executable/mac/idp/bin/idp")).getAbsolutePath();
		String filePath = file.getAbsolutePath();
		String command = idpPath + " " + filePath;
		String result = getTerminal().runCommand(command);
		getTerminal().execute("unlink " + file.getAbsolutePath(), true);
		return result;
	}

	private File createFile(final String string) {
		final File file = FileManager.instance.createTempFile("idp");
		getTerminal().execute("mkfifo " + file.getAbsolutePath(), true);
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
					writer.write(string);
					writer.close();
				} catch(IOException e) {
					throw new IllegalStateException("Unexpected error.", e);
				}
			}
		};
		executorService.submit(runnable);
		return file;
	}
	//endregion
}
