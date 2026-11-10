package lol.ethan.may.asm.transformers.render;

import lol.ethan.may.asm.Transformer;
import lol.ethan.may.events.player.RenderLivingEvent;
import lol.ethan.may.events.render.RenderModelEvent;
import lol.ethan.may.housing.AntiInvis;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class RendererLivingEntityTransformer extends Transformer {
    private static final Class<?> compClass;
    private static final Type ANTI_INVIS_TYPE = Type.getType(AntiInvis.class);

    public RendererLivingEntityTransformer() {
        super(RendererLivingEntity.class);
    }

    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if ("(Lnet/minecraft/entity/EntityLivingBase;DDDLcom/llamalad7/mixinextras/injector/wrapoperation/Operation;)V".equals(method.desc)) {
                for (final AbstractInsnNode abstractNode : method.instructions) {
                    if (abstractNode instanceof MethodInsnNode) {
                        final MethodInsnNode mn = (MethodInsnNode) abstractNode;
                        if ("bridge$getDisplayNameComponent".equals(mn.name)) {
                            method.instructions.insertBefore(mn, asm(a -> {
                                a.pop();
                                a.load(1, Type.getType(EntityLivingBase.class));
                                a.invokevirtual(Type.getInternalName(EntityLivingBase.class), "getDisplayName",
                                        "()Lnet/minecraft/util/IChatComponent;", false);
                                a.invokeinterface(Type.getInternalName(IChatComponent.class), "getFormattedText", "()Ljava/lang/String;");

                                a.invokestatic(Type.getInternalName(RendererLivingEntityTransformer.class), "textToComponent",
                                        "(Ljava/lang/String;)Ljava/lang/Object;", false);
                            }));
                            method.instructions.remove(mn);
                        }
                    }
                }
            } else if ("canRenderName".equals(method.name)) {
                method.instructions.insert(asm(a -> {
                    final Label isNotPlayer = new Label();

                    a.load(1, Type.getType(EntityLivingBase.class));
                    a.instanceOf(Type.getType(EntityPlayer.class));

                    a.ifeq(isNotPlayer);
                    a.getstatic(ANTI_INVIS_TYPE.getInternalName(), "INVISIBLE", "Ljava/util/Set;");
                    a.load(1, Type.getType(EntityLivingBase.class));
                    a.invokeinterface("java/util/Set", "contains", "(Ljava/lang/Object;)Z");
                    final Label isInvis = new Label();

                    a.ifeq(isInvis);
                    a.iconst(1);
                    a.areturn(Type.BOOLEAN_TYPE);
                    a.mark(isInvis);
                    a.mark(isNotPlayer);
                }));
            } else if ("doRender".equals(method.name) && "(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V".equals(method.desc)) {
                insertBeforeReturn(method, asm(a -> {
                    a.anew(Type.getType(RenderLivingEvent.Post.class));
                    a.dup();

                    a.load(1, Type.getType(EntityLivingBase.class));
                    a.load(0, Type.getType(RendererLivingEntity.class));
                    a.load(2, Type.DOUBLE_TYPE);
                    a.load(4, Type.DOUBLE_TYPE);
                    a.load(6, Type.DOUBLE_TYPE);
                    a.load(9, Type.FLOAT_TYPE);
                    a.invokespecial(Type.getInternalName(RenderLivingEvent.Post.class), "<init>",
                            "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/entity/RendererLivingEntity;DDDF)V", false);
                    postEvent(a);
                    a.pop();
                }));
                method.instructions.insert(asm(a -> {
                    a.anew(Type.getType(RenderLivingEvent.Pre.class));
                    a.dup();

                    a.load(1, Type.getType(EntityLivingBase.class));
                    a.load(0, Type.getType(RendererLivingEntity.class));
                    a.load(2, Type.DOUBLE_TYPE);
                    a.load(4, Type.DOUBLE_TYPE);
                    a.load(6, Type.DOUBLE_TYPE);
                    a.load(9, Type.FLOAT_TYPE);
                    a.invokespecial(Type.getInternalName(RenderLivingEvent.Pre.class), "<init>",
                            "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/entity/RendererLivingEntity;DDDF)V", false);
                    postEvent(a);
                    ifEventCancel(a, asm -> asm.areturn(Type.VOID_TYPE));
                }));
            } else if ("renderModel".equals(method.name) && "(Lnet/minecraft/entity/EntityLivingBase;FFFFFF)V".equals(method.desc)) {
                insertBeforeReturn(method, asm(a -> {
                    a.anew(Type.getType(RenderModelEvent.Post.class));
                    a.dup();

                    a.load(1, Type.getType(EntityLivingBase.class));
                    a.invokespecial(Type.getInternalName(RenderModelEvent.Post.class), "<init>",
                            "(Lnet/minecraft/entity/EntityLivingBase;)V", false);
                    postEvent(a);
                    a.pop();
                }));
                method.instructions.insert(asm(a -> {
                    a.anew(Type.getType(RenderModelEvent.Pre.class));
                    a.dup();

                    a.load(1, Type.getType(EntityLivingBase.class));
                    a.invokespecial(Type.getInternalName(RenderModelEvent.Pre.class), "<init>",
                            "(Lnet/minecraft/entity/EntityLivingBase;)V", false);
                    postEvent(a);
                    ifEventCancel(a, asm -> asm.areturn(Type.VOID_TYPE));
                }));
            }

        }
    }

    public static Object may$modifyDisplayName(final Object componentObj, final Object entityObj) {
        if (!(entityObj instanceof EntityLivingBase) || componentObj == null) {
            return componentObj;
        }
        final EntityLivingBase entity = (EntityLivingBase) entityObj;
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
    public boolean shouldComputeFrames() {
        return true;
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
