package lol.ethan.may.asm.transformers.minecraft;

import lol.ethan.may.asm.Transformer;
import lol.ethan.may.events.WorldEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class WorldTransformer extends Transformer {
    public WorldTransformer() {
        super(Minecraft.class);
    }
    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if (!"(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V".equals(method.desc)) continue;
            method.instructions.insert(asm(a -> {
                a.load(0, Type.getType(Minecraft.class));
                a.getfield("net/minecraft/client/Minecraft", mapField(Minecraft.class, "theWorld"), "L" + Type.getInternalName(WorldClient.class) + ";");
                final Label ifNotNull = new Label();
                final Label ifNull = new Label();

                a.ifnull(ifNotNull);

                a.anew(Type.getType(WorldEvent.Unload.class));
                a.dup();
                a.invokespecial(Type.getInternalName(WorldEvent.Unload.class), "<init>", "()V", false);
                postEvent(a);
                a.pop();

                a.goTo(ifNull);

                a.mark(ifNotNull);

                a.anew(Type.getType(WorldEvent.Load.class));
                a.dup();
                a.invokespecial(Type.getInternalName(WorldEvent.Load.class), "<init>", "()V", false);
                postEvent(a);
                a.pop();
                a.mark(ifNull);
            }));
        }
    }

    @Override
    public boolean shouldComputeFrames() {
        return true;
    }
}
