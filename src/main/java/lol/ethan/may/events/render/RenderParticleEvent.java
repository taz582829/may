package lol.ethan.may.events.render;

import lol.ethan.may.events.Event;
import net.minecraft.client.particle.EntityFX;

import java.lang.reflect.Field;

public class RenderParticleEvent extends Event {
    private static Field scale;
    private final float partialTicks;
    private float rotationX;
    private float rotationZ;
    private float rotationYZ;
    private float rotationXY;
    private float rotationXZ;
    private final EntityFX particle;

    static {
        try {
            scale = EntityFX.class.getDeclaredField("particleScale");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
    public RenderParticleEvent(final float partialTicks, final float rotationX, final float rotationZ, final float rotationYZ, final float rotationXY, final float rotationXZ, final EntityFX particle) {
        this.partialTicks = partialTicks;
        this.rotationX = rotationX;
        this.rotationZ = rotationZ;
        this.rotationYZ = rotationYZ;
        this.rotationXY = rotationXY;
        this.rotationXZ = rotationXZ;
        this.particle = particle;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public float getRotationX() {
        return rotationX;
    }

    public void setRotationX(final float rotationX) {
        this.rotationX = rotationX;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public void setRotationZ(final float rotationZ) {
        this.rotationZ = rotationZ;
    }

    public float getRotationYZ() {
        return rotationYZ;
    }

    public void setRotationYZ(final float rotationYZ) {
        this.rotationYZ = rotationYZ;
    }

    public float getRotationXY() {
        return rotationXY;
    }

    public void setRotationXY(final float rotationXY) {
        this.rotationXY = rotationXY;
    }

    public float getRotationXZ() {
        return rotationXZ;
    }

    public void setRotationXZ(final float rotationXZ) {
        this.rotationXZ = rotationXZ;
    }

    public float getScale() {
        try {
            return scale.getFloat(particle);
        } catch (final Exception e) {
            return 1.0F;
        }
    }
    public void setScale(final float scale) {
        try {
            RenderParticleEvent.scale.set(particle, scale);
        } catch (final Exception ignored) {}
    }

    public EntityFX getParticle() {
        return particle;
    }

    public static class Pre extends RenderParticleEvent {
        public Pre(final float partialTicks, final float rotationX, final float rotationZ, final float rotationYZ, final float rotationXY, final float rotationXZ, final EntityFX particle) {
            super(partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ, particle);
        }
    }
    public static class Post extends RenderParticleEvent {
        public Post(final float partialTicks, final float rotationX, final float rotationZ, final float rotationYZ, final float rotationXY, final float rotationXZ, final EntityFX particle) {
            super(partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ, particle);
        }
    }
}
