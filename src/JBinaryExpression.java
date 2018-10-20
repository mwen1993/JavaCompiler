// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a binary expression. A binary expression has an operator and
 * two operands: a lhs and a rhs.
 */

abstract class JBinaryExpression extends JExpression {

    /** The binary operator. */
    protected String operator;

    /** The lhs operand. */
    protected JExpression lhs;

    /** The rhs operand. */
    protected JExpression rhs;

    /**
     * Construct an AST node for a binary expression given its line number, the
     * binary operator, and lhs and rhs operands.
     * 
     * @param line
     *            line in which the binary expression occurs in the source file.
     * @param operator
     *            the binary operator.
     * @param lhs
     *            the lhs operand.
     * @param rhs
     *            the rhs operand.
     */

    protected JBinaryExpression(int line, String operator, JExpression lhs,
            JExpression rhs) {
        super(line);
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JBinaryExpression line=\"%d\" type=\"%s\" "
                + "operator=\"%s\">\n", line(), ((type == null) ? "" : type
                .toString()), Util.escapeSpecialXMLChars(operator));
        p.indentRight();
        p.printf("<Lhs>\n");
        p.indentRight();
        lhs.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Lhs>\n");
        p.printf("<Rhs>\n");
        p.indentRight();
        rhs.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Rhs>\n");
        p.indentLeft();
        p.printf("</JBinaryExpression>\n");
    }

}

/**
 * The AST node for a plus (+) expression. In j--, as in Java, + is overloaded
 * to denote addition for numbers and concatenation for Strings.
 */

class JPlusOp extends JBinaryExpression {

    /**
     * Construct an AST node for an addition expression given its line number,
     * and the lhs and rhs operands.
     * 
     * @param line
     *            line in which the addition expression occurs in the source
     *            file.
     * @param lhs
     *            the lhs operand.
     * @param rhs
     *            the rhs operand.
     */

    public JPlusOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "+", lhs, rhs);
    }

    /**
     * Analysis involves first analyzing the operands. If this is a string
     * concatenation, we rewrite the subtree to make that explicit (and analyze
     * that). Otherwise we check the types of the addition operands and compute
     * the result type.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        lhs = (JExpression) lhs.analyze(context);
        rhs = (JExpression) rhs.analyze(context);
        if (lhs.type() == Type.STRING || rhs.type() == Type.STRING) {
            return (new JStringConcatenationOp(line, lhs, rhs))
                    .analyze(context);
        } else if (lhs.type() == Type.INT && rhs.type() == Type.INT) {
            type = Type.INT;
        } else if (lhs.type() == Type.INT && rhs.type() == Type.DOUBLE) {
            type = Type.INT;
        } else if (lhs.type() == Type.DOUBLE && rhs.type() == Type.INT) {
            type = Type.DOUBLE;
        } else if (lhs.type() == Type.DOUBLE && rhs.type() == Type.DOUBLE) {
            type = Type.DOUBLE;
        } else {
            type = Type.ANY;
            JAST.compilationUnit.reportSemanticError(line(),
                    "Invalid operand types for +");
        }
        return this;
    }

    /**
     * Any string concatenation has been rewritten as a JStringConcatenationOp
     * (in analyze()), so code generation here involves simply generating code
     * for loading the operands onto the stack and then generating the
     * appropriate add instruction.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
        if (type == Type.INT) {
            lhs.codegen(output);
            rhs.codegen(output);
            if(lhs.type() == Type.INT && rhs.type() == Type.DOUBLE){
                output.addNoArgInstruction(D2I);
            }
            output.addNoArgInstruction(IADD);
        } else if (type == Type.DOUBLE){
            lhs.codegen(output);
            rhs.codegen(output);
            if(lhs.type() == Type.DOUBLE && rhs.type() == Type.INT){
                output.addNoArgInstruction(I2D);
            }
            output.addNoArgInstruction(DADD);
        }
    }

}

/**
 * The AST node for a subtraction (-) expression.
 */

class JSubtractOp extends JBinaryExpression {

    /**
     * Construct an AST node for a subtraction expression given its line number,
     * and lhs and rhs operands.
     * 
     * @param line
     *            line in which the subtraction expression occurs in the source
     *            file.
     * @param lhs
     *            the lhs operand.
     * @param rhs
     *            the rhs operand.
     */

    public JSubtractOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "-", lhs, rhs);
    }

    /**
     * Analyzing the - operation involves analyzing its operands, checking
     * types, and determining the result type.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        lhs = (JExpression) lhs.analyze(context);
        rhs = (JExpression) rhs.analyze(context);
        if (lhs.type() == Type.INT && rhs.type() == Type.INT) {
            type = Type.INT;
        } else if (lhs.type() == Type.INT && rhs.type() == Type.DOUBLE) {
            type = Type.INT;
        } else if (lhs.type() == Type.DOUBLE && rhs.type() == Type.INT) {
            type = Type.DOUBLE;
        } else if (lhs.type() == Type.DOUBLE && rhs.type() == Type.DOUBLE) {
            type = Type.DOUBLE;
        }
        return this;
    }

    /**
     * Generating code for the - operation involves generating code for the two
     * operands, and then the subtraction instruction.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
        if (type == Type.INT) {
            lhs.codegen(output);
            rhs.codegen(output);
            if(lhs.type() == Type.INT && rhs.type() == Type.DOUBLE){
                output.addNoArgInstruction(D2I);
            }
            output.addNoArgInstruction(ISUB);
        } else if (type == Type.DOUBLE){
            lhs.codegen(output);
            rhs.codegen(output);
            if(lhs.type() == Type.DOUBLE && rhs.type() == Type.INT){
                output.addNoArgInstruction(I2D);
            }
            output.addNoArgInstruction(DSUB);
        }
    }

}

/**
 * The AST node for a multiplication (*) expression.
 */

class JMultiplyOp extends JBinaryExpression {

    /**
     * Construct an AST for a multiplication expression given its line number,
     * and the lhs and rhs operands.
     * 
     * @param line
     *            line in which the multiplication expression occurs in the
     *            source file.
     * @param lhs
     *            the lhs operand.
     * @param rhs
     *            the rhs operand.
     */

    public JMultiplyOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "*", lhs, rhs);
    }

    /**
     * Analyzing the * operation involves analyzing its operands, checking
     * types, and determining the result type.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        lhs = (JExpression) lhs.analyze(context);
        rhs = (JExpression) rhs.analyze(context);
        if (lhs.type() == Type.INT && rhs.type() == Type.INT) {
            type = Type.INT;
        } else if (lhs.type() == Type.INT && rhs.type() == Type.DOUBLE) {
            type = Type.INT;
        } else if (lhs.type() == Type.DOUBLE && rhs.type() == Type.INT) {
            type = Type.DOUBLE;
        } else if (lhs.type() == Type.DOUBLE && rhs.type() == Type.DOUBLE) {
            type = Type.DOUBLE;
        }
        return this;
    }

    /**
     * Generating code for the * operation involves generating code for the two
     * operands, and then the multiplication instruction.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
        if (type == Type.INT) {
            lhs.codegen(output);
            rhs.codegen(output);
            if(lhs.type() == Type.INT && rhs.type() == Type.DOUBLE){
                output.addNoArgInstruction(D2I);
            }
            output.addNoArgInstruction(IMUL);
        } else if (type == Type.DOUBLE){
            lhs.codegen(output);
            rhs.codegen(output);
            if(lhs.type() == Type.DOUBLE && rhs.type() == Type.INT){
                output.addNoArgInstruction(I2D);
            }
            output.addNoArgInstruction(DMUL);
        }
    }

}

/**
 * The AST node for a division (/) expression.
 */

class JDivideOp extends JBinaryExpression {

    /**
     * Construct an AST for a division expression given its line number,
     * and the lhs and rhs operands.
     * 
     * @param line
     *            line in which the division expression occurs in the
     *            source file.
     * @param lhs
     *            the lhs operand.
     * @param rhs
     *            the rhs operand.
     */

    public JDivideOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "/", lhs, rhs);
    }

    /**
     * Analyzing the / operation involves analyzing its operands, checking
     * types, and determining the result type.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        lhs = (JExpression) lhs.analyze(context);
        rhs = (JExpression) rhs.analyze(context);
        if (lhs.type() == Type.INT && rhs.type() == Type.INT) {
            type = Type.INT;
        } else if (lhs.type() == Type.INT && rhs.type() == Type.DOUBLE) {
            type = Type.INT;
        } else if (lhs.type() == Type.DOUBLE && rhs.type() == Type.INT) {
            type = Type.DOUBLE;
        } else if (lhs.type() == Type.DOUBLE && rhs.type() == Type.DOUBLE) {
            type = Type.DOUBLE;
        }
        return this;
    }

    /**
     * Generating code for the / operation involves generating code for the two
     * operands, and then the division instruction.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
        if (type == Type.INT) {
            lhs.codegen(output);
            rhs.codegen(output);
            if(lhs.type() == Type.INT && rhs.type() == Type.DOUBLE){
                output.addNoArgInstruction(D2I);
            }
            output.addNoArgInstruction(IDIV);
        } else if (type == Type.DOUBLE){
            lhs.codegen(output);
            rhs.codegen(output);
            if(lhs.type() == Type.DOUBLE && rhs.type() == Type.INT){
                output.addNoArgInstruction(I2D);
            }
            output.addNoArgInstruction(DDIV);
        }
    }

}

/**
 * The AST node for a modulus (%) expression.
 */

class JModuloOp extends JBinaryExpression {

    /**
     * Construct an AST for a modulus expression given its line number,
     * and the lhs and rhs operands.
     * 
     * @param line
     *            line in which the modulus expression occurs in the
     *            source file.
     * @param lhs
     *            the lhs operand.
     * @param rhs
     *            the rhs operand.
     */

    public JModuloOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "%", lhs, rhs);
    }

    /**
     * Analyzing the % operation involves analyzing its operands, checking
     * types, and determining the result type.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        lhs = (JExpression) lhs.analyze(context);
        rhs = (JExpression) rhs.analyze(context);
        if (lhs.type() == Type.INT && rhs.type() == Type.INT) {
            type = Type.INT;
        } else if (lhs.type() == Type.INT && rhs.type() == Type.DOUBLE) {
            type = Type.INT;
        } else if (lhs.type() == Type.DOUBLE && rhs.type() == Type.INT) {
            type = Type.DOUBLE;
        } else if (lhs.type() == Type.DOUBLE && rhs.type() == Type.DOUBLE) {
            type = Type.DOUBLE;
        }
        return this;
    }

    /**
     * Generating code for the % operation involves generating code for the two
     * operands, and then the modulus instruction.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
        if (type == Type.INT) {
            lhs.codegen(output);
            rhs.codegen(output);
            if(lhs.type() == Type.INT && rhs.type() == Type.DOUBLE){
                output.addNoArgInstruction(D2I);
            }
            output.addNoArgInstruction(IREM);
        } else if (type == Type.DOUBLE){
            lhs.codegen(output);
            rhs.codegen(output);
            if(lhs.type() == Type.DOUBLE && rhs.type() == Type.INT){
                output.addNoArgInstruction(I2D);
            }
            output.addNoArgInstruction(DREM);
        }
    }

}

/**
 * The AST node for a BitwiseOr (|) expression.
 */

class JBitwiseOrOp extends JBinaryExpression {

    /**
     * Construct an AST for a BitwiseOr expression given its line number,
     * and the lhs and rhs operands.
     * 
     * @param line
     *            line in which the BitwiseOr expression occurs in the
     *            source file.
     * @param lhs
     *            the lhs operand.
     * @param rhs
     *            the rhs operand.
     */

    public JBitwiseOrOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "|", lhs, rhs);
    }

    /**
     * Analyzing the | operation involves analyzing its operands, checking
     * types, and determining the result type.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        lhs = (JExpression) lhs.analyze(context);
        rhs = (JExpression) rhs.analyze(context);
        lhs.type().mustMatchExpected(line(), Type.INT);
        rhs.type().mustMatchExpected(line(), Type.INT);
        type = Type.INT;
        return this;
    }

    /**
     * Generating code for the | operation involves generating code for the two
     * operands, and then the BitwiseOr instruction.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
        lhs.codegen(output);
        rhs.codegen(output);
        output.addNoArgInstruction(IOR);
    }

}

/**
 * The AST node for a BitwiseExclusiveOr (^) expression.
 */

class JBitwiseExclusiveOrOp extends JBinaryExpression {

    /**
     * Construct an AST for a BitwiseExclusiveOr expression given its line number,
     * and the lhs and rhs operands.
     * 
     * @param line
     *            line in which the BitwiseExclusiveOr expression occurs in the
     *            source file.
     * @param lhs
     *            the lhs operand.
     * @param rhs
     *            the rhs operand.
     */

    public JBitwiseExclusiveOrOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "^", lhs, rhs);
    }

    /**
     * Analyzing the ^ operation involves analyzing its operands, checking
     * types, and determining the result type.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        lhs = (JExpression) lhs.analyze(context);
        rhs = (JExpression) rhs.analyze(context);
        lhs.type().mustMatchExpected(line(), Type.INT);
        rhs.type().mustMatchExpected(line(), Type.INT);
        type = Type.INT;
        return this;
    }

    /**
     * Generating code for the ^ operation involves generating code for the two
     * operands, and then the BitwiseExclusiveOr instruction.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
        lhs.codegen(output);
        rhs.codegen(output);
        output.addNoArgInstruction(IXOR);
    }

}

/**
 * The AST node for a BitwiseAnd (&) expression.
 */

class JBitwiseAndOp extends JBinaryExpression {

    /**
     * Construct an AST for a BitwiseAnd expression given its line number,
     * and the lhs and rhs operands.
     * 
     * @param line
     *            line in which the BitwiseAnd expression occurs in the
     *            source file.
     * @param lhs
     *            the lhs operand.
     * @param rhs
     *            the rhs operand.
     */

    public JBitwiseAndOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "&", lhs, rhs);
    }

    /**
     * Analyzing the & operation involves analyzing its operands, checking
     * types, and determining the result type.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        lhs = (JExpression) lhs.analyze(context);
        rhs = (JExpression) rhs.analyze(context);
        lhs.type().mustMatchExpected(line(), Type.INT);
        rhs.type().mustMatchExpected(line(), Type.INT);
        type = Type.INT;
        return this;
    }

    /**
     * Generating code for the & operation involves generating code for the two
     * operands, and then the BitwiseAnd instruction.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
        lhs.codegen(output);
        rhs.codegen(output);
        output.addNoArgInstruction(IAND);
    }

}

