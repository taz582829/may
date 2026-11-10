package lol.ethan.may.config;

public class ConfigEvent {
    private boolean cancelled;

    public void setCancelled(final boolean status) {
        cancelled = status;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
