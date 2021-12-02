package me.onils.customautogg;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class Transformer implements ClassFileTransformer {
    String ggMessage;
    String levelHeadMessage;
    Transformer(String ggMsg, String levelHeadMessage){
        this.ggMessage = ggMsg;
        this.levelHeadMessage = levelHeadMessage;
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

                for(MethodNode method : cn.methods){
                    for(AbstractInsnNode insn : method.instructions){
                        if(insn.getOpcode() == Opcodes.LDC){
                            LdcInsnNode ldc = (LdcInsnNode) insn;
                            if("/achat gg".equals(ldc.cst)){
                                ldc.cst = "/achat " + ggMessage;
                            }else if("Level: ".equals(ldc.cst)){
                                ldc.cst = levelHeadMessage;
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
