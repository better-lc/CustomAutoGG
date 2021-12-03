package me.onils.customautogg;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class Transformer implements ClassFileTransformer {
    public String ggMessage;

    Transformer(String ggMessage){
        this.ggMessage = ggMessage;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (classfileBuffer == null || classfileBuffer.length == 0) {
            return new byte[0];
        }

        if(className.startsWith("lunar/")){
            ClassReader cr = new ClassReader(classfileBuffer);
            if(cr.getSuperName().startsWith("lunar/") && cr.getInterfaces().length == 1){
                ClassWriter cw = new ClassWriter(cr, 0);

                cr.accept(new ClassVisitor(Opcodes.ASM9, cw) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                                                     String[] exceptions) {
                        return new MethodVisitor(Opcodes.ASM9, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                            @Override
                            public void visitLdcInsn(Object value) {
                                if("/achat gg".equals(value))
                                    super.visitLdcInsn(ggMessage);
                                super.visitLdcInsn(value);
                            }
                        };
                    }
                }, 0);

                return cw.toByteArray();
            }
        }
        return classfileBuffer;
    }
}