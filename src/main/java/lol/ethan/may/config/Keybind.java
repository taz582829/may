package lol.ethan.may.config;

public class Keybind {
    private final Runnable action;
    private int bind;

    public Keybind(int bind, Runnable action) {
        this.bind = bind;
        this.action = action;
    }

    public int getBind() {
        return this.bind;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public Runnable getAction() {
        return this.action;
    }
}

