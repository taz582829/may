package lol.ethan.may.events.render;

import lol.ethan.may.events.Event;
import net.minecraft.entity.EntityLivingBase;

public class RenderModelEvent extends Event {
    private final EntityLivingBase entity;

    public RenderModelEvent(final EntityLivingBase entity) {
        this.entity = entity;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }

    public static class Pre extends RenderModelEvent {
        public Pre(final EntityLivingBase entity) {
            super(entity);
        }
    }
    public static class Post extends RenderModelEvent {
        public Post(final EntityLivingBase entity) {
            super(entity);
        }
    }
}
