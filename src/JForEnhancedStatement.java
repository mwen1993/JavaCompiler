package jminusminus;

/**
 * The AST node for a Enhanced For Statement.
 */

public class JForEnhancedStatement extends JStatement {
	
	/** Type of variable */
	protected Type type;
	
	/** Name of variable */
	protected String name;
	
	/** Expression */
	protected JExpression expression;
	
	/** Enhanced for body */
	protected JStatement body;
	
	/**
     * Construct an AST node for an Enhanced for statement given its line number, the
     * type of variable, variable name, Expression and enhanced for body
     * 
     * @param line
     *            line in which the enhanced for statement occurs in the source file.
     * @param Type
     *            Type of variable
     * @param name
     * 			  Name of variable
     * @param expression
     * 			  Expression
     * @param body
     *            enhanced for body.
     */
	
	protected JForEnhancedStatement(int line, Type type, String name, JExpression expression, JStatement body) {
		super(line);
		this.type = type;
		this.name = name;
		this.expression = expression;
		this.body = body;
	}

	@Override
	public JAST analyze(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void codegen(CLEmitter output) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeToStdOut(PrettyPrinter p) {
		p.printf("<JForEnhancedStatement line=\"%d\" type=\"%s\" name=\"%s\">\n",
				line(), type, name);
        p.indentRight();
        p.printf("<Expression>\n");
        p.indentRight();
        expression.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Expression>\n");
        p.printf("<Body>\n");
        p.indentRight();
        body.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Body>\n");
        p.indentLeft();
        p.printf("</JForEnhancedStatement>\n");
		
	}

}
