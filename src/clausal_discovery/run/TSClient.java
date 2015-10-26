package clausal_discovery.run;

import clausal_discovery.configuration.Configuration;
import clausal_discovery.validity.ValidatedClause;
import idp.IdpExpressionPrinter;
import log.Log;

import java.util.List;

/**
 * Created by samuelkolb on 24/07/15.
 *
 * @author Samuel Kolb
 */
public class TSClient {

	//region Variables

	//endregion

	//region Construction

	//endregion

	//region Public methods
	public static void main(String[] args) {
		int variableCount = 1;
		int clauseLength = 4;


		List<ValidatedClause> clauses = new RunClient().run(Configuration.fromLocalFile("tablesetting",
				variableCount, clauseLength));
		Log.LOG.newLine();
		clauses.forEach(vc -> Log.LOG.formatLine("%s", IdpExpressionPrinter.print(vc.getClause().getFormula())));
	}
	//endregion
}
