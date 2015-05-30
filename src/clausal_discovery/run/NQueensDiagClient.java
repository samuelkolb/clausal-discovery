package clausal_discovery.run;

import clausal_discovery.configuration.Configuration;
import clausal_discovery.validity.ValidatedClause;
import idp.IdpExpressionPrinter;
import log.Log;

import java.util.List;

/**
 * Created by samuelkolb on 13/04/15.
 *
 * @author Samuel Kolb
 */
public class NQueensDiagClient {

	/**
	 * Run the nqueens smart example
	 * @param args	Ignored command line arguments
	 */
	public static void main(String[] args) {
		List<ValidatedClause> clauses = new RunClient().run(Configuration.fromLocalFile("nqueens_diag", 2, 2));
		for(ValidatedClause clause : clauses)
			Log.LOG.printLine(IdpExpressionPrinter.print(clause.getClause().getFormula()) + ".");
	}
}
