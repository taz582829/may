package lol.ethan.may.events;

public class RenderWorldEvent extends Event {
    private final float partialTicks;

    public RenderWorldEvent(final float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}

