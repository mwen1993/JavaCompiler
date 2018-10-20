package jminusminus;
import static jminusminus.CLConstants.*;
/** The AST node for a Conditional Operator ?:
 */

public class JConditionalOperator extends JExpression {
	
	/**The condition*/
	protected JExpression lhs;
	
	/**Expression to be executed when condition is true*/
	protected JExpression rhsYes;
	
	/**Expression to be executed when condition is false*/
	protected JExpression rhsNo;
	
	/**
	 * Construct an AST for conditional Operator given its 
	 * line number, lhs, rhsYes, rhsNo
	 * 
	 * @param line
	 * 			line in which the conditional operator occurs in the source
     *          file.
	 * @param lhs
	 * 			condition to be evaluated
	 * @param rhsYes
	 * 			Expression to be executed when condition is true
	 * @param rhsNo
	 * 			Expression to be executed when condition is false
	 */
	
	protected JConditionalOperator(int line, JExpression lhs, JExpression rhsYes, JExpression rhsNo) {
		super(line);
		this.lhs = lhs;
		this.rhsYes = rhsYes;
		this.rhsNo = rhsNo;
	}

	@Override
	public JExpression analyze(Context context) {
		lhs = (JExpression) lhs.analyze(context);
        lhs.type().mustMatchExpected(line(), Type.BOOLEAN);
        rhsYes = (JExpression) rhsYes.analyze(context);
        if (rhsNo != null) {
            rhsNo = (JExpression) rhsNo.analyze(context);
        }
        return this;
	}

	@Override
	public void codegen(CLEmitter output) {
		String elseLabel = output.createLabel();
        String endLabel = output.createLabel();
        lhs.codegen(output, elseLabel, false);
        rhsYes.codegen(output);
        if (rhsNo != null) {
            output.addBranchInstruction(GOTO, endLabel);
        }
        output.addLabel(elseLabel);
        if (rhsNo != null) {
            rhsNo.codegen(output);
            output.addLabel(endLabel);
        }
		
	}

	@Override
	public void writeToStdOut(PrettyPrinter p) {
		 p.printf("<JConditionalExpression line=\"%d\" type=\"%s\" "
	                + "operator=\"%s\">\n", line(), ((type == null) ? "" : type
	                        .toString()), Util.escapeSpecialXMLChars("?:"));
	        p.indentRight();
	        p.printf("<Lhs>\n");
	        p.indentRight();
	        lhs.writeToStdOut(p);
	        p.indentLeft();
	        p.printf("</Lhs>\n");
	        p.printf("<RhsYes>\n");
	        p.indentRight();
	        rhsYes.writeToStdOut(p);
	        p.indentLeft();
	        p.printf("</RhsYes>\n");
	        p.printf("<RhsNo>\n");
	        p.indentRight();
	        rhsNo.writeToStdOut(p);
	        p.indentLeft();
	        p.printf("</RhsNo>\n");
	        p.indentLeft();
	        p.printf("</JConditionalExpression>\n");
		
	}

}
