package jminusminus;

/**
 * The AST node for a static block.
 */

public class JStaticBlock extends JAST implements JMember {
	
	/** The static block body */
	protected JBlock block;
	
	/**
	 * Construct an AST of a static block 
	 * given the line number and static block body
	 * 
	 * @param line  line in which static block appears 
	 * 				in the source file.
	 * 
	 * @param body  static block body
	 */
	JStaticBlock(int line, JBlock body) {
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
		p.printf("<JStaticBlock line=\"%d\">\n", line());
		p.indentRight();
		if (block != null) {
            p.println("<Static Block Body>");
            p.indentRight();
            block.writeToStdOut(p);
            p.indentLeft();
            p.println("</Static Block Body>");
        }
        p.indentLeft();
        p.println("</JStaticBlock>");
	}

}
