package jminusminus;

/**The AST of a Label statement */

public class JLabelStatement extends JStatement {
	
	/** Name of Label */
	protected String labelName;
	
	/** Label Statement */
	protected JStatement statement;
	
	/**
	 * Construct an AST for label statement given the 
	 * line number, Label Name and label statement
	 * 
	 * @param line
	 * 			line in which the label-statement appears
     *          in the source file.
	 * @param labelName
	 * 			Name of Label
	 * @param statement
	 * 			Label Statement
	 */
	
	protected JLabelStatement(int line, String labelName, JStatement statement) {
		super(line);
		this.statement = statement;
		this.labelName = labelName;
		
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
		 p.printf("<JLabelStatement line=\"%d\" labelName=\"%s\"/>\n", line(), labelName);
         p.indentRight();
         statement.writeToStdOut(p);
         p.indentLeft();
         p.printf("</JLabelStatement>\n");
	}

}
