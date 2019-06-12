package semantic.syntaxTree.declaration;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import semantic.symbolTable.Display;
import semantic.symbolTable.SymbolTable;
import semantic.symbolTable.descriptor.DSCP;
import semantic.symbolTable.descriptor.hastype.VariableDSCP;
import semantic.symbolTable.descriptor.type.TypeDSCP;
import semantic.syntaxTree.declaration.method.MethodDCL;
import semantic.syntaxTree.program.ClassDCL;

import java.util.Optional;

public class VariableDCL extends Declaration {
    private String type;
    private TypeDSCP typeDSCP;
    private boolean initialized;

    public VariableDCL(String name, String type, boolean isConstant, boolean initialized) {
        super(name, isConstant);
        this.type = type;
        this.initialized = initialized;
    }

    public TypeDSCP getTypeDSCP() {
        if (typeDSCP == null) {
            Optional<DSCP> fetchedDSCP = Display.find(type);
            if (!fetchedDSCP.isPresent() || !(fetchedDSCP.get() instanceof TypeDSCP))
                throw new RuntimeException("Type " + type + " not found");
            typeDSCP = (TypeDSCP) fetchedDSCP.get();
        }
        return typeDSCP;
    }

    @Override
    public void generateCode(ClassDCL currentClass, MethodDCL currentMethod, ClassVisitor cv, MethodVisitor mv, Label breakLabel, Label continueLabel) {
        // Only check current block table
        // otherwise this declaration shadows other declarations
        SymbolTable top = Display.top();
        if (top.contain(getName()))
            throw new RuntimeException(getName() + " declared more than one time");

        getTypeDSCP();
        VariableDSCP variableDSCP = new VariableDSCP(getName(), getTypeDSCP(), getTypeDSCP().getSize(),
                top.getFreeAddress(), isConstant(), initialized);
        top.addSymbol(getName(), variableDSCP);
    }
}
