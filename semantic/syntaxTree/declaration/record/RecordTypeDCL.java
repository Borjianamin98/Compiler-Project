package semantic.syntaxTree.declaration.record;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import semantic.exception.DuplicateDeclarationException;
import semantic.symbolTable.Display;
import semantic.symbolTable.SymbolTable;
import semantic.symbolTable.descriptor.RecordTypeDSCP;
import semantic.syntaxTree.Node;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class RecordTypeDCL extends Node {
    private String name;
    private List<Field> fields;

    public RecordTypeDCL(String name, List<Field> fields) {
        this.name = name;
        this.fields = fields;
    }

    @Override
    public void generateCode(ClassVisitor cv, MethodVisitor mv) {
        // Generate Code
        int recordSize = 0;
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, name, null, "java/lang/Object", null);

        for (Field field : fields) {
            classWriter.visitField(Opcodes.ACC_PUBLIC, field.getName(), field.getDescriptor(), null, null).visitEnd();
            recordSize += field.getType().getSize();
        }

        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        for (Field field : fields) {
            if (field.getDefaultValue() != null) {
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0); // load "this"
                field.getDefaultValue().generateCode(classWriter, methodVisitor);
                methodVisitor.visitFieldInsn(Opcodes.PUTFIELD, name, field.getName(), field.getDescriptor());
            }
        }
        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();

        classWriter.visitEnd();

        try (FileOutputStream fos = new FileOutputStream(Node.outputPath + name + ".class")) {
            fos.write(classWriter.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update SymbolTable
        // Only check current block table
        // otherwise this declaration shadows other declarations
        SymbolTable top = Display.top();
        if (top.contain(name)) {
            throw new DuplicateDeclarationException(name + " declared more than one time");
        }
        // TODO size of record is: recordSize or 1 (pointer size)
        RecordTypeDSCP recordDSCP = new RecordTypeDSCP(name, 1, fields);
        top.addType(name, recordDSCP);
    }
}