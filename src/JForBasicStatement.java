package jminusminus;

import java.util.ArrayList;
import static jminusminus.CLConstants.*;
/**
 * The AST node for a Basic For Statement.
 */

public class JForBasicStatement extends JStatement {
	
	/** forInit Statements */
	protected ArrayList<JStatement> forInitStatements;
	
	/** forInit Declarations */
	protected ArrayList<JVariableDeclarator> forInitDeclarations;
	
	/** Test expression. */
	protected JExpression condition;
	
	/** forUpdate statements */
	protected ArrayList<JStatement> forUpdate;
	
	/** Basic For body */
	protected JStatement body;
	
	 /**
     * Construct an AST node for a basic for statement given its line number, the
     * forInit Statements or forInit Declarations, test expression,
     * forUpdate Statements and the basic for body.
     * 
     * @param line
     *            line in which the basic for statement occurs in the source file.
     * @param forInitStatements
     *            forInit Statements
     * @param forInitDeclarations
     * 			  forInit Declarations
     * @param condition
     * 			  Test Expression
     * @param body
     *            basic for body.
     */
	
	protected JForBasicStatement(int line, ArrayList<JStatement> forInitStatements,
			ArrayList<JVariableDeclarator> forInitDeclarations, JExpression condition, 
			ArrayList<JStatement> forUpdate, JStatement body) {
		super(line);
		this.forInitStatements = forInitStatements;
		this.forInitDeclarations = forInitDeclarations;
		this.condition = condition;
		this.forUpdate = forUpdate;
		this.body = body;
	}

	@Override
	public JAST analyze(Context context) {
		for(JVariableDeclarator forInitDeclaration : forInitDeclarations){
			forInitDeclaration.analyze(context);
		}
		for(JStatement forInitStatement : forInitStatements){
			forInitStatement.analyze(context);
		}
		condition.type().mustMatchExpected(line(), Type.BOOLEAN);
        body = (JStatement) body.analyze(context);

        for(JStatement update : forUpdate){
        	update.analyze(context);
        }
        return this;
	}

	@Override
	public void codegen(CLEmitter output) {
		for(JVariableDeclarator forInitDeclaration : forInitDeclarations){
			forInitDeclaration.codegen(output);
		}
		for(JStatement forInitStatement : forInitStatements){
			forInitStatement.codegen(output);
		}
		String test = output.createLabel();
        String out = output.createLabel();

        // Branch out of the loop on the test condition
        // being false
        output.addLabel(test);
        condition.codegen(output, out, false);

        // Codegen body
        body.codegen(output);

        //update statement
        for(JStatement update : forUpdate){
        	update.codegen(output);
        }

        // Unconditional jump back up to test
        output.addBranchInstruction(GOTO, test);

        // The label below and outside the loop
        output.addLabel(out);
    }
	

	@Override
	public void writeToStdOut(PrettyPrinter p) {
		p.printf("<JForBasicStatement line=\"%d\">\n", line());
		
		p.indentRight();
		if(forInitStatements != null){
			p.println("<ForInitStatements>");
            p.indentRight();
            for (JStatement statement : forInitStatements) {
                statement.writeToStdOut(p);
            }
            p.indentLeft();
            p.println("</ForInitStatements>");
		}
		
		if(forInitDeclarations != null){
			p.printf("<ForInitDeclarations>\n");
        	p.indentRight();
            for (JVariableDeclarator declaration : forInitDeclarations) {
                declaration.writeToStdOut(p);
            }
            p.indentLeft();
            p.println("</ForInitDeclarations>");
		}
		
		if(condition != null){
			p.printf("<TestExpression>\n");
	        p.indentRight();
	        condition.writeToStdOut(p);
	        p.indentLeft();
	        p.printf("</TestExpression>\n");
		}
        
		if(forUpdate != null){
			p.printf("<ForUpdate>\n");
	        p.indentRight();
	        for (JStatement statement : forUpdate) {
	            statement.writeToStdOut(p);
	        }
	        p.indentLeft();
	        p.printf("</ForUpdate>\n");
		}
       
        p.printf("<Body>\n");
        p.indentRight();
        body.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Body>\n");
        p.indentLeft();
        p.printf("</JForBasicStatement>\n");
	}

}
