package lol.ethan.may.events.player;

import lol.ethan.may.events.Event;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

public class RenderLivingEvent {

    public static class Post
            extends Event {
        private final EntityLivingBase entity;
        private final RendererLivingEntity<EntityLivingBase> renderer;
        private final double x;
        private final double y;
        private final double z;

        public Post(EntityLivingBase entity, RendererLivingEntity<EntityLivingBase> renderer, double x, double y, double z) {
            this.entity = entity;
            this.renderer = renderer;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public EntityLivingBase getEntity() {
            return this.entity;
        }

        public RendererLivingEntity<EntityLivingBase> getRenderer() {
            return this.renderer;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }
    }

    public static class Pre
            extends Event {
        private final EntityLivingBase entity;
        private final RendererLivingEntity<EntityLivingBase> renderer;
        private final double x;
        private final double y;
        private final double z;

        public Pre(EntityLivingBase entity, RendererLivingEntity<EntityLivingBase> renderer, double x, double y, double z) {
            this.entity = entity;
            this.renderer = renderer;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public EntityLivingBase getEntity() {
            return this.entity;
        }

        public RendererLivingEntity<EntityLivingBase> getRenderer() {
            return this.renderer;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }
    }
}

