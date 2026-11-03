package lol.ethan.may.events.render;

import lol.ethan.may.events.Event;

public class RenderGameOverlayEvent extends Event {
    private final float partialTicks;

    public RenderGameOverlayEvent(final float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public static class Pre extends RenderGameOverlayEvent {
        public Pre(final float partialTicks) {
            super(partialTicks);
        }
    }
    public static class Post extends RenderGameOverlayEvent {
        public Post(final float partialTicks) {
            super(partialTicks);
        }
    }
}
