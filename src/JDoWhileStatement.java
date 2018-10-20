package jminusminus;
import static jminusminus.CLConstants.*;

/**
 * The AST node for a do while-statement.
 */

public class JDoWhileStatement extends JStatement {
	
	/** Test expression. */
    protected JExpression condition;

    /** The body. */
    protected JStatement body;
    
    /**
     * Construct an AST node for a do while-statement given its line number, the
     * test expression, and the body.
     * 
     * @param line
     *            line in which the do while-statement occurs in the source file.
     * @param body
     *            the body
     * @param condition
     *            the test Expression.
     */
	protected JDoWhileStatement(int line, JStatement body, JExpression condition) {
		super(line);
		this.body = body;
		this.condition = condition;
	}

	@Override
	public JAST analyze(Context context) {
		condition = condition.analyze(context);
        condition.type().mustMatchExpected(line(), Type.BOOLEAN);
        body = (JStatement) body.analyze(context);
        return this;
	}

	@Override
	public void codegen(CLEmitter output) {
		// Need two labels
        String test = output.createLabel();
        String out = output.createLabel();

        // Branch out of the loop on the test condition
        // being false
        output.addLabel(test);
        // Codegen body
        body.codegen(output);
        condition.codegen(output, out, false);

        

        // Unconditional jump back up to test
        output.addBranchInstruction(GOTO, test);

        // The label below and outside the loop
        output.addLabel(out);
		
	}

	@Override
	public void writeToStdOut(PrettyPrinter p) {
		p.printf("<JDoWhileStatement line=\"%d\">\n", line());
        p.indentRight();
        p.printf("<Body>\n");
        p.indentRight();
        body.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Body>\n");
        p.printf("<TestExpression>\n");
        p.indentRight();
        condition.writeToStdOut(p);
        p.indentLeft();
        p.printf("</TestExpression>\n");
        p.indentLeft();
        p.printf("</JDoWhileStatement>\n");
	}

}
