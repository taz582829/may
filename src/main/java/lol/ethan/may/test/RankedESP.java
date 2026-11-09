package lol.ethan.may.test;

import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.render.RenderWorldEvent;
import lol.ethan.may.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFootStepFX;
import net.minecraft.util.AxisAlignedBB;

import javax.annotation.Nonnull;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankedESP {
    private static final Map<Integer, float[]> coords = new HashMap<>();
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Field particlesField = EffectRenderer.class.getDeclaredField("fxLayers");

    public RankedESP() throws NoSuchFieldException {
    }

    @SuppressWarnings("unchecked")
    private List<EntityFX>[][] getParticles() {
        try {
            return (List<EntityFX>[][]) particlesField.get(mc.effectRenderer);
        } catch (final Exception ignored) {
            return null;
        }
    }

    @SubscribeEvent
    public void onRenderWorld(final RenderWorldEvent event) {
        final List<EntityFX>[][] layers = getParticles();
        if (layers == null) return;
        for (final List<EntityFX>[] row : layers) {
            for (final List<EntityFX> list : row) {
                for (final EntityFX particle : list) {
                    if (!(particle instanceof EntityFootStepFX)) continue;
                    final AxisAlignedBB bb = getBoundingBox(event, particle);
                    RenderUtils.drawBoundingBox(bb, Color.red, 3);
                }
            }
        }
    }

    @Nonnull
    private static AxisAlignedBB getBoundingBox(final RenderWorldEvent event, final EntityFX particle) {
        final double x = particle.prevPosX + (particle.posX - particle.prevPosX) * event.getPartialTicks();
        final double y = particle.prevPosY + (particle.posY - particle.prevPosY) * event.getPartialTicks();
        final double z = particle.prevPosZ + (particle.posZ - particle.prevPosZ) * event.getPartialTicks();

        final AxisAlignedBB bb = new AxisAlignedBB(
                x - 0.3,
                y,
                z - 0.3,
                x + 0.3,
                y + 1.8,
                z + 0.3
        );
        return bb;
    }
}
