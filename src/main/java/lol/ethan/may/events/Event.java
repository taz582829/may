package lol.ethan.may.events;

import com.google.common.base.Objects;

public class Event {
    private boolean cancelled;

    public boolean isCancellable() {
        return true;
    }
    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        if (!isCancellable()) throw new RuntimeException("trying to cancel " + getClass().getName() + " but it's not cancellable!");
        this.cancelled = cancelled;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("cancelled", cancelled)
                .toString();
    }
}

