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
public class ColoringMinimalClient {

	/**
	 * Run the coloring minimal example
	 * @param args	Ignored command line arguments
	 */
	public static void main(String[] args) {
		int variableCount = 3;
		int clauseLength = 3;

		if(args.length > 0)
			variableCount = Integer.parseInt(args[0]);

		if(args.length > 1)
			clauseLength = Integer.parseInt(args[1]);

		List<ValidatedClause> clauses = new RunClient().run(Configuration.fromLocalFile("coloring_minimal",
				variableCount, clauseLength));
		Log.LOG.newLine();
		clauses.forEach(vc -> Log.LOG.formatLine("%s", IdpExpressionPrinter.print(vc.getClause().getFormula())));
	}
}
