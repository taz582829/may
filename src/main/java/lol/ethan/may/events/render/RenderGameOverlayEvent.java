package lol.ethan.may.events.render;

import lol.ethan.may.events.Event;

public class RenderGameOverlayEvent {
    public static class Pre extends Event {
        private final float partialTicks;

        public Pre(float partialTicks) {
            this.partialTicks = partialTicks;
        }

        public float getPartialTicks() {
            return partialTicks;
        }
    }
    public static class Post extends Event {
        private final float partialTicks;

        public Post(float partialTicks) {
            this.partialTicks = partialTicks;
        }

        public float getPartialTicks() {
            return partialTicks;
        }
    }
}
