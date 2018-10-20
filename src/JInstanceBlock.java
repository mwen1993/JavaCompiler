package jminusminus;

/**
 * The AST node for an instance block.
 */

public class JInstanceBlock extends JAST implements JMember {
	
	/** The instance block body */
	protected JBlock block;
	
	/**
	 * Construct an AST of an instance block 
	 * given the line number and instance block body
	 * 
	 * @param line  line in which instance block appears 
	 * 				in the source file.
	 * 
	 * @param body  instance block body
	 */
	JInstanceBlock(int line, JBlock body) {
		super(line);
		this.block = body;
	}
	
	public void preAnalyze(Context context, CLEmitter partial) {
		
	}
	
	public JAST analyze(Context context) {
		
		return null;
	}

	public void codegen(CLEmitter output) {
		
		
	}

	public void writeToStdOut(PrettyPrinter p) {
		p.printf("<JInstanceBlock line=\"%d\">\n", line());
		p.indentRight();
		if (block != null) {
            p.println("<Instance Block Body>");
            p.indentRight();
            block.writeToStdOut(p);
            p.indentLeft();
            p.println("</Instance Block Body>");
        }
        p.indentLeft();
        p.println("</JInstanceBlock>");
	}

}
