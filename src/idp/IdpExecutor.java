package idp;

import basic.StringUtil;
import cern.colt.bitvector.BitMatrix;
import idp.program.EntailsProgram;
import idp.program.IdpProgram;
import idp.program.ValidityProgram;
import log.Log;
import logic.theory.InlineTheory;
import logic.theory.KnowledgeBase;
import logic.theory.LogicExecutor;
import time.Stopwatch;
import vector.Vector;

import java.util.ArrayList;
import java.util.List;

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

	public final Stopwatch entailmentStopwatch = new Stopwatch();

	public int entailmentCount = 0;

	public int noEntailmentCount = 0;

	// IVAR fileManager - The file manager used for temporary files

	private final FileManager fileManager;

	protected FileManager getFileManager() {
		return fileManager;
	}

	private final IdpProgramPrinter printer = new IdpProgramPrinter.Cached();

	//endregion

	//region Construction
	private IdpExecutor() {
		this.fileManager = new FileManager("temp");
		getFileManager().cleanTempDir("idp");
	}
	//endregion

	//region Public methods

	@Override
	public BitMatrix testValidityTheories(KnowledgeBase knowledgeBase) {
		String result = executeSafe(new ValidityProgram(knowledgeBase));
		List<Vector<Boolean>> list = new ArrayList<>();
		for(String line : result.trim().split("\n"))
			if(line.trim().length() > 0)
				list.add(new Vector<>(line.trim().split(" ")).map(Boolean.class, this::getBoolean));
		BitMatrix bitMatrix = new BitMatrix(knowledgeBase.getStructures().size(), knowledgeBase.getTheories().size());
		for(int row = 0; row < list.size(); row++)
			for(int col = 0; col < list.get(row).size(); col++)
				bitMatrix.put(col, row, list.get(row).get(col));
		return bitMatrix;
	}

	@Override
	public boolean entails(KnowledgeBase knowledgeBase, InlineTheory theory) {
		entailmentStopwatch.start();
		boolean test = executeTest(new EntailsProgram(knowledgeBase, theory));
		entailmentStopwatch.pause();
		entailmentCount++;
		if(!test)
			noEntailmentCount++;
		return test;
	}

	private boolean executeTest(IdpProgram idpProgram) throws IllegalStateException {
		String string = executeSafe(idpProgram).trim();
		try {
			return getBoolean(string);
		} catch(Exception e) {
			Log.LOG.printTitle("Error occurred:").printLine(e.getMessage()).newLine();
			Log.LOG.printTitle("Program:").printLine(getDebugString(idpProgram));
			throw e;
		}
	}

	private boolean getBoolean(String string) {
		if("YES".equals(string))
			return true;
		if("NO".equals(string))
			return false;
		throw new IllegalStateException("Incorrect output: " + string);
	}

	private String executeSafe(IdpProgram idpProgram) throws IllegalStateException {
		try {
			return execute(idpProgram);
		} catch(Exception e) {
			e.printStackTrace();
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
		program.setPrinter(printer);
		return IDP.execute(program.print());
	}

	//endregion
}
