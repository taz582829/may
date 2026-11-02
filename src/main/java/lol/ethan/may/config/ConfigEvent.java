package lol.ethan.may.config;

public class ConfigEvent {
    private boolean cancelled = false;

    public void setCancelled(boolean status) {
        cancelled = status;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
