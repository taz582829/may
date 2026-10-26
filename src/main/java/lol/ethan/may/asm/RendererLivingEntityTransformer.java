package lol.ethan.may.asm;

import net.minecraft.entity.EntityLivingBase;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Collections;
import java.util.List;

public class RendererLivingEntityTransformer implements ITransformer {
    private static final Class<?> compClass;

    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if (!method.desc.equals("(Lnet/minecraft/entity/EntityLivingBase;DDDLcom/llamalad7/mixinextras/injector/wrapoperation/Operation;)V")) continue;
            for (final AbstractInsnNode abstractNode : method.instructions) {
                if (!(abstractNode instanceof MethodInsnNode)) continue;
                final MethodInsnNode insn = (MethodInsnNode)abstractNode;
                if (!insn.name.startsWith("bridge$getDisplayNameComponent")) continue;
                final InsnList instructions = new InsnList();
                instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(RendererLivingEntityTransformer.class), "may$modifyDisplayName", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false));
                instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, "com/moonsworth/lunar/lib/adventure/text/Component"));
                method.instructions.insert(insn, instructions);
                method.instructions.remove(insn);
            }
        }
    }

    public static Object may$modifyDisplayName(final Object componentObj, final Object entityObj) {
        if (!(entityObj instanceof EntityLivingBase) || null == componentObj) {
            return componentObj;
        }
        final EntityLivingBase entity = (EntityLivingBase)entityObj;
        return RendererLivingEntityTransformer.textToComponent(entity.getDisplayName().getFormattedText());
    }

    public static Object textToComponent(final String text) {
        try {
            return compClass.getMethod("text", String.class).invoke(null, text);
        }
        catch (final Exception e) {
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
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
