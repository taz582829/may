package lol.ethan.may.events;

public class RenderGameOverlayEvent {

    public static class Post extends Event {
        private final float partialTicks;

        public Post(final float partialTicks) {
            this.partialTicks = partialTicks;
        }

        public float getPartialTicks() {
            return partialTicks;
        }
    }

    public static class Pre extends Event {
        private final float partialTicks;

        public Pre(final float partialTicks) {
            this.partialTicks = partialTicks;
        }

        public float getPartialTicks() {
            return partialTicks;
        }
    }
}

