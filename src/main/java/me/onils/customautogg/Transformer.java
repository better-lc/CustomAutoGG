package me.onils.customautogg;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Transformer implements ClassFileTransformer {
    String ggMessage;
    Transformer(String ggMsg){
        this.ggMessage = ggMsg;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (classfileBuffer == null || classfileBuffer.length == 0) {
            return new byte[0];
        }

        if(className.startsWith("lunar/")){
            ClassReader cr = new ClassReader(classfileBuffer);
            if(cr.getSuperName().startsWith("lunar/") && cr.getInterfaces().length == 1){
                ClassNode cn = new ClassNode();

                cr.accept(cn, 0);

                outer:
                for(MethodNode method : cn.methods){
                    for(AbstractInsnNode insn : method.instructions){
                        if(insn.getOpcode() == Opcodes.LDC){
                            LdcInsnNode ldc = (LdcInsnNode) insn;
                            if("/achat gg".equals(ldc.cst)){
                                ldc.cst = "/achat " + ggMessage;
                                break outer;
                            }
                        }
                    }
                }

                ClassWriter cw = new ClassWriter(cr, 0);
                cn.accept(cw);

                return cw.toByteArray();
            }
        }
        return classfileBuffer;
    }
}
