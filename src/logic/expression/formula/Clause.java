package logic.expression.formula;

import basic.ArrayUtil;
import log.Log;
import vector.Vector;
import logic.expression.visitor.ExpressionLogicPrinter;
import logic.expression.visitor.ExpressionVisitor;

import java.util.HashSet;
import java.util.List;

/**
 * Created by samuelkolb on 23/10/14.
 *
 * @author Samuel Kolb
 */
public class Clause extends Formula {

	//region Variables
	private final Vector<Atom> head;

	public Vector<Atom> getHeadAtoms() {
		return head;
	}

	public Formula getHead() {
		if(getHeadAtoms().length > 1)
			return new Or(getHeadAtoms().toArray(new Formula[getHeadAtoms().size()]));
		if(getHeadAtoms().length == 1)
			return getHeadAtoms().e(0);
		return LogicalValue.FALSE;
	}

	private final Vector<Atom> body;

	public Vector<Atom> getBodyAtoms() {
		return body;
	}

	public Formula getBody() {
		if(getBodyAtoms().length > 1)
			return new And(getBodyAtoms());
		if(getBodyAtoms().length == 1)
			return getBodyAtoms().e(0);
		return LogicalValue.TRUE;
	}

	//endregion

	//region Construction

	private Clause(Atom[] body, Atom[] head) {
		this(new Vector<>(body), new Vector<>(head));
	}

	private Clause(Vector<Atom> body, Vector<Atom> head) {
		this.head = head;
		this.body = body;
	}

	//endregion

	//region Public methods

	/**
	 * Creates a new fact (clause without body)
	 * @param head	The head atoms
	 * @return	An corresponding clause object
	 */
	public static Clause fact(Atom... head) {
		return new Clause(new Atom[0], head);
	}

	/**
	 * Creates a new horn clause (clause with exactly one head atom)
	 * @param head	The head atom
	 * @param body	The body atoms
	 * @return	An corresponding clause object
	 */
	public static Clause horn(Atom head, Atom... body) {
		return new Clause(body, new Atom[]{head});
	}

	/**
	 * Creates a new clause
	 * @param head	The head atoms
	 * @param body	The body atoms
	 * @return	An corresponding clause object
	 */
	public static Clause clause(Atom[] head, Atom... body) {
		return new Clause(body, head);
	}

	/**
	 * Creates a new clause
	 * @param body	The body atoms
	 * @param head	The head atoms
	 * @return	An corresponding clause object
	 */
	public static Clause clause(List<Atom> body, List<Atom> head) {
		return new Clause(body.toArray(new Atom[body.size()]), head.toArray(new Atom[head.size()]));
	}

	/**
	 * Creates a new condition (clause without head)
	 * @param body	The body atoms
	 * @return	An corresponding clause object
	 */
	public static Clause condition(Atom... body) {
		return new Clause(body, new Atom[0]);
	}

	/**
	 * Creates a clause that adds the given atom to the body of this clause
	 * @param atom	The atom to add
	 * @return	A new clause
	 */
	public Clause expandBody(Atom atom) {
		Atom[] array = getBodyAtoms().toArray(new Atom[getBodyAtoms().size()]);
		Atom[] body = ArrayUtil.addElement(array, atom);
		if(getHeadAtoms().size() > 0) {
			Log.LOG.printLine("- Expression: " + ExpressionLogicPrinter.print(getHeadAtoms().get(0)));
		}
		Atom[] head = getHeadAtoms().toArray(new Atom[getHeadAtoms().size()]);
		return new Clause(body, head);
	}

	/**
	 * Creates a clause that adds the given atom to the head of this clause
	 * @param atom	The atom to add
	 * @return	A new clause
	 */
	public Clause expandHead(Atom atom) {
		return new Clause(getBodyAtoms(), getHeadAtoms().grow(atom));
	}

	public boolean isEmpty() {
		return getBodyAtoms().isEmpty() && getHeadAtoms().isEmpty();
	}

	@Override
	public boolean isTrue() {
		return new Implication(getBody(), getHead()).isTrue();
	}

	@Override
	public boolean isGround() {
		for(Atom atom : getBodyAtoms())
			if(!atom.isGround())
				return false;
		for(Atom atom : getHeadAtoms())
			if(!atom.isGround())
				return false;
		return true;
	}

	@Override
	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);
	}

	public boolean contains(Atom atom) {
		for(Atom bodyAtom : getBodyAtoms())
			if(bodyAtom.equals(atom))
				return true;
		for(Atom headAtom : getHeadAtoms())
			if(headAtom.equals(atom))
				return true;
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Clause clause = (Clause) o;

		return new HashSet<>(body).equals(new HashSet<>(clause.body))
				&& new HashSet<>(head).equals(new HashSet<>(clause.head));

	}

	@Override
	public int hashCode() {
		int result = head.hashCode();
		result = 31 * result + body.hashCode();
		return result;
	}

	//endregion

}
