package jminusminus;

import java.util.ArrayList;

public class JInterfaceDeclaration extends JAST implements JTypeDecl {
	
	 /** Interface modifiers. */
    private ArrayList<String> mods;
    
    /** Interface name. */
    private String name;

    /** Interface block. */
    private ArrayList<JMember> interfaceBlock;

    /** Super interface type. */
    private ArrayList<Type> superInterface;
    
    /**
     * Construct an AST node for an interface declaration given the line number, list
     * of class modifiers, name of the interface, list of super interfaces and the 
     * interface block.
     * 
     * @param line
     * 			line in which the interface declaration occurs in the source file.
     * @param mods
     * 			interface modifiers
     * @param name
     * 			interface name
     * @param superInterface
     * 			extends Interfaces
     * @param interfaceBlock
     * 			interface block
     */
    
	public JInterfaceDeclaration(int line, ArrayList<String> mods, String name, ArrayList<Type> superInterface,
			ArrayList<JMember> interfaceBlock  ) {
		super(line);
		 this.mods = mods;
	     this.name = name;
	     this.superInterface = superInterface;
	     this.interfaceBlock = interfaceBlock;
	}

	@Override
	public void declareThisType(Context context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preAnalyze(Context context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type superType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type thisType() {
		// TODO Auto-generated method stub
		return null;
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

	public void writeToStdOut(PrettyPrinter p) {
		 p.printf("<JInterfaceDeclaration line=\"%d\" name=\"%s\">\n", line(), name);
	        p.indentRight();
	        if (superInterface != null) {
	            p.println("<Extends>");
	            p.indentRight();
	            for (Type interfaces : superInterface) {
	                p.printf("<Interface name=\"%s\"/>\n", interfaces.toString());
	            }
	            p.indentLeft();
	            p.println("</Extends>");
	        }
	       
	        if (mods != null) {
	            p.println("<Modifiers>");
	            p.indentRight();
	            for (String mod : mods) {
	                p.printf("<Modifier name=\"%s\"/>\n", mod);
	            }
	            p.indentLeft();
	            p.println("</Modifiers>");
	        }
	        if (interfaceBlock != null) {
	            p.println("<InterfaceBlock>");
	            for (JMember member : interfaceBlock) {
	                ((JAST) member).writeToStdOut(p);
	            }
	            p.println("</InterfaceBlock>");
	        }
	        p.indentLeft();
	        p.println("</JInterfaceDeclaration>");
		
	}

}
