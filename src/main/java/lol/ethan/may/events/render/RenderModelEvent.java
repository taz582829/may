package lol.ethan.may.events.render;

import lol.ethan.may.events.Event;
import net.minecraft.entity.EntityLivingBase;

public class RenderModelEvent extends Event {
    private final EntityLivingBase entity;

    public RenderModelEvent(EntityLivingBase entity) {
        this.entity = entity;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }

    public static class Pre extends RenderModelEvent {
        public Pre(EntityLivingBase entity) {
            super(entity);
        }
    }
    public static class Post extends RenderModelEvent {
        public Post(EntityLivingBase entity) {
            super(entity);
        }
    }
}
