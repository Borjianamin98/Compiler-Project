package semantic.syntaxTree.statement.assignment;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import semantic.syntaxTree.declaration.method.MethodDCL;
import semantic.syntaxTree.expression.Expression;
import semantic.syntaxTree.expression.binaryOperation.arithmetic.Multiply;
import semantic.syntaxTree.expression.identifier.Variable;
import semantic.syntaxTree.program.ClassDCL;

public class MultiplyAssignment extends Assignment {
    public MultiplyAssignment(Variable variable, Expression value) {
        super(variable, value);
    }

    @Override
    public void generateCode(ClassDCL currentClass, MethodDCL currentMethod, ClassVisitor cv, MethodVisitor mv) {
        Multiply multiply = new Multiply(getVariable(), getValue());
        DirectAssignment assignment = new DirectAssignment(getVariable(), multiply);
        assignment.generateCode(currentClass, currentMethod, cv, mv);
    }
}
