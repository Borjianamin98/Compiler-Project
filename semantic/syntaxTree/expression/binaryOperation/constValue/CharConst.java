package semantic.syntaxTree.expression.binaryOperation.constValue;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import semantic.symbolTable.Constants;
import semantic.syntaxTree.expression.Expression;

public class CharConst extends Expression {
    public char value;

    public CharConst(char value) {
        super(Constants.INTEGER_DSCP);
        this.value = value;
    }

    @Override
    public void generateCode(ClassVisitor cv, MethodVisitor mv) {
        mv.visitLdcInsn((int) value);
    }
}
