package lol.ethan.may.events.render;

import lol.ethan.may.events.Event;

public class RenderWorldEvent extends Event {
    private final float partialTicks;

    public RenderWorldEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
