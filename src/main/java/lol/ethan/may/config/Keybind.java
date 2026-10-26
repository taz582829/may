package lol.ethan.may.config;

public class Keybind {
    private final Runnable action;
    private int bind;

    public Keybind(final int bind, final Runnable action) {
        this.bind = bind;
        this.action = action;
    }

    public int getBind() {
        return bind;
    }

    public void setBind(final int bind) {
        this.bind = bind;
    }

    public Runnable getAction() {
        return action;
    }
}

