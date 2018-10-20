package jminusminus;

/**
 * The AST node for a break-statement.
 */

public class JBreakStatement extends JStatement{
	
	/** Name of Label */
	protected String labelName;
	
	/**
	 * Construct an AST of break statement given the line 
	 * and label name
	 * 
	 * @param line
	 * 			line in which the break-statement appears
     *          in the source file.
	 * @param labelName
	 * 			Name of Label
	 */
	
	protected JBreakStatement(int line, String labelName) {
		super(line);
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
		if (labelName != null) {
            p.printf("<JBreakStatement line=\"%d\" labelName=\"%s\"/>\n", line(), labelName);
        } else {
            p.printf("<JBreakStatement line=\"%d\"/>\n", line());
        }
	}

}
