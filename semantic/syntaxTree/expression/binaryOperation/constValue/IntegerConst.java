package semantic.syntaxTree.expression.binaryOperation.constValue;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import semantic.symbolTable.Constants;
import semantic.symbolTable.Utility;
import semantic.syntaxTree.expression.Expression;

public class IntegerConst extends Expression {
    public int value;

    public IntegerConst(int value) {
        super(Constants.INTEGER_DSCP);
        this.value = value;
    }

    @Override
    public void generateCode(ClassVisitor cv, MethodVisitor mv) {
        if (value >= 0 && value <= 5)
            mv.visitInsn(Utility.getOpcode("I", "CONST", "_" + value));
        else
            mv.visitLdcInsn(value);
    }
}
