package lol.ethan.may.events.player;

import lol.ethan.may.events.Event;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

public class RenderLivingEvent extends Event {
    private final EntityLivingBase entity;
    private final RendererLivingEntity<EntityLivingBase> renderer;
    private final double x;
    private final double y;
    private final double z;
    private final float partialTicks;

    public RenderLivingEvent(final EntityLivingBase entity, final RendererLivingEntity<EntityLivingBase> renderer, final double x, final double y, final double z, final float partialTicks) {
        this.entity = entity;
        this.renderer = renderer;
        this.x = x;
        this.y = y;
        this.z = z;
        this.partialTicks = partialTicks;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }

    public RendererLivingEntity<EntityLivingBase> getRenderer() {
        return renderer;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public static class Post extends RenderLivingEvent {
        public Post(final EntityLivingBase entity, final RendererLivingEntity<EntityLivingBase> renderer, final double x, final double y, final double z, final float partialTicks) {
            super(entity, renderer, x, y, z, partialTicks);
        }
    }

    public static class Pre extends RenderLivingEvent {
        public Pre(final EntityLivingBase entity, final RendererLivingEntity<EntityLivingBase> renderer, final double x, final double y, final double z, final float partialTicks) {
            super(entity, renderer, x, y, z, partialTicks);
        }
    }
}

