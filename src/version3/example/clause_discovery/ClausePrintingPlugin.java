package version3.example.clause_discovery;

import log.Log;
import version3.algorithm.Node;
import version3.algorithm.Plugin;
import version3.algorithm.Result;
import idp.IdpExpressionPrinter;

import java.util.List;

/**
 * The clause printing plugin prints out nodes visited during the clausal discovery algorithm.
 * Because of the indexing scheme used by clauses, the regular printing plugin cannot be used.
 */
public class ClausePrintingPlugin implements Plugin<StatusClause> {

	//region Variables
	private final VariableRefinement refinement;
	//endregion

	//region Construction

	public ClausePrintingPlugin(VariableRefinement refinement) {
		this.refinement = refinement;
	}

	//endregion

	//region Public methods

	@Override
	public void initialise(List<Node<StatusClause>> initialNodes, Result<StatusClause> result) {
		Log.LOG.printLine("Initial nodes: " + toString(initialNodes));
	}

	@Override
	public boolean nodeSelected(Node<StatusClause> node) {
		return true;
	}

	@Override
	public void nodeProcessed(Node<StatusClause> node, Result<StatusClause> result) {
		Log.LOG.printObjects("Processed node: ", toString(node));
	}

	@Override
	public void nodeExpanded(Node<StatusClause> node, List<Node<StatusClause>> childNodes) {
		Log.LOG.printObjects("\tYields: ", toString(childNodes));
	}

	private String toString(List<Node<StatusClause>> nodes) {
		if(nodes.isEmpty())
			return "";
		StringBuilder builder = new StringBuilder(toString(nodes.get(0)));
		for(int i = 1; i < nodes.size(); i++)
			builder.append(", ").append(toString(nodes.get(i)));
		return builder.toString();
	}

	private String toString(Node<StatusClause> node) {
		return IdpExpressionPrinter.print(refinement.getClause(node.getValue()));
	}

	//endregion
}
