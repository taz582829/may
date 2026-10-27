package lol.ethan.may.events.player;

import lol.ethan.may.events.Event;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

public class RenderLivingEvent {

    public static class Post extends Event {
        private final EntityLivingBase entity;
        private final RendererLivingEntity<EntityLivingBase> renderer;
        private final double x;
        private final double y;
        private final double z;

        public Post(final EntityLivingBase entity, final RendererLivingEntity<EntityLivingBase> renderer, final double x, final double y, final double z) {
            this.entity = entity;
            this.renderer = renderer;
            this.x = x;
            this.y = y;
            this.z = z;
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
    }

    public static class Pre extends Event {
        private final EntityLivingBase entity;
        private final RendererLivingEntity<EntityLivingBase> renderer;
        private final double x;
        private final double y;
        private final double z;

        public Pre(final EntityLivingBase entity, final RendererLivingEntity<EntityLivingBase> renderer, final double x, final double y, final double z) {
            this.entity = entity;
            this.renderer = renderer;
            this.x = x;
            this.y = y;
            this.z = z;
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
    }
}

