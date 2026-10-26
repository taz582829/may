package lol.ethan.may.asm;

import java.util.Collections;
import java.util.List;
import lol.ethan.may.asm.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import net.minecraft.entity.EntityLivingBase;

public class RendererLivingEntityTransformer
implements ITransformer {
    private static final Class<?> compClass;

    @Override
    public void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (!method.desc.equals("(Lnet/minecraft/entity/EntityLivingBase;DDDLcom/llamalad7/mixinextras/injector/wrapoperation/Operation;)V")) continue;
            for (AbstractInsnNode abstractNode : method.instructions) {
                if (!(abstractNode instanceof MethodInsnNode)) continue;
                MethodInsnNode insn = (MethodInsnNode)abstractNode;
                if (!insn.name.startsWith("bridge$getDisplayNameComponent")) continue;
                InsnList instructions = new InsnList();
                instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(RendererLivingEntityTransformer.class), "may$modifyDisplayName", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false));
                instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, "com/moonsworth/lunar/lib/adventure/text/Component"));
                method.instructions.insert(insn, instructions);
                method.instructions.remove(insn);
            }
        }
    }

    public static Object may$modifyDisplayName(Object componentObj, Object entityObj) {
        if (!(entityObj instanceof EntityLivingBase) || componentObj == null) {
            return componentObj;
        }
        EntityLivingBase entity = (EntityLivingBase)entityObj;
        return RendererLivingEntityTransformer.textToComponent(entity.getDisplayName().getFormattedText());
    }

    public static Object textToComponent(String text) {
        try {
            return compClass.getMethod("text", String.class).invoke(null, text);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.client.renderer.entity.RendererLivingEntity");
    }

    static {
        try {
            compClass = Class.forName("com.moonsworth.lunar.lib.adventure.text.Component");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
