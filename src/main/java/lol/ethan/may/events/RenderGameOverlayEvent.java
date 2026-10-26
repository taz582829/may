package lol.ethan.may.events;

public class RenderGameOverlayEvent {

    public static class Post
            extends Event {
        private final float partialTicks;

        public Post(float partialTicks) {
            this.partialTicks = partialTicks;
        }

        public float getPartialTicks() {
            return this.partialTicks;
        }
    }

    public static class Pre
            extends Event {
        private final float partialTicks;

        public Pre(float partialTicks) {
            this.partialTicks = partialTicks;
        }

        public float getPartialTicks() {
            return this.partialTicks;
        }
    }
}

