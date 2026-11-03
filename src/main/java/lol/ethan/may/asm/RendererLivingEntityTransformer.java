package lol.ethan.may.asm;

import lol.ethan.may.events.player.RenderLivingEvent;
import lol.ethan.may.events.render.RenderModelEvent;
import lol.ethan.may.housing.AntiInvis;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class RendererLivingEntityTransformer extends Transformer {
    private static final Class<?> compClass;
    private static final Type ANTI_INVIS_TYPE = Type.getType(AntiInvis.class);

    static {
        try {
            compClass = Class.forName("com.moonsworth.lunar.lib.adventure.text.Component");
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public RendererLivingEntityTransformer() {
        super(RendererLivingEntity.class);
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
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if (method.desc.equals("(Lnet/minecraft/entity/EntityLivingBase;DDDLcom/llamalad7/mixinextras/injector/wrapoperation/Operation;)V")) {
                for (final AbstractInsnNode abstractNode : method.instructions) {
                    if (abstractNode instanceof MethodInsnNode) {
                        final MethodInsnNode mn = (MethodInsnNode) abstractNode;
                        if (mn.name.equals("bridge$getDisplayNameComponent")) {
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
            } else if (method.name.equals("canRenderName")) {
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
            } else if (method.name.equals("doRender") && method.desc.equals("(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V")) {
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
            } else if (method.name.equals("renderModel") && method.desc.equals("(Lnet/minecraft/entity/EntityLivingBase;FFFFFF)V")) {
                insertBeforeReturn(method, asm(a -> {
                    a.anew(Type.getType(RenderModelEvent.Post.class));
                    a.dup();

                    a.load(1, Type.getType(EntityLivingBase.class));
                    a.invokespecial(Type.getInternalName(RenderModelEvent.Post.class), "<init>",
                            "(Lnet/minecraft/entity/EntityLivingBase;)V", false);
                    postEvent(a);
                    a.pop();
                    //ifEventCancel(a, asm -> asm.areturn(Type.VOID_TYPE));
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

    @Override
    public boolean shouldComputeFrames() {
        return true;
    }
}
