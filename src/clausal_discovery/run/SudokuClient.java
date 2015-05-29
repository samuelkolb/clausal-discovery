package clausal_discovery.run;

import clausal_discovery.configuration.Configuration;
import clausal_discovery.validity.ValidatedClause;
import idp.IdpExpressionPrinter;
import log.Log;
import log.PrefixFilter;

import java.util.List;

/**
 * Created by samuelkolb on 13/04/15.
 *
 * @author Samuel Kolb
 */
public class SudokuClient {

	/**
	 * Run the sudoku example
	 * @param args	Ignored command line arguments
	 */
	public static void main(String[] args) {
		List<ValidatedClause> clauses = new RunClient().run(Configuration.fromLocalFile("sudoku_light", 4, 4));
		Log.LOG.newLine().newLine();
		for(ValidatedClause clause : clauses)
			Log.LOG.printLine(IdpExpressionPrinter.print(clause.getClause().getFormula()) + ".");
	}
}
