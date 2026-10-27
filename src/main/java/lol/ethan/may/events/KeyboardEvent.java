package lol.ethan.may.events;

public class KeyboardEvent extends Event {
    private int bind;
    private boolean state;

    public KeyboardEvent(final int bind, final boolean state) {
        this.bind = bind;
        this.state = state;
    }

    public int getBind() {
        return bind;
    }

    public void setBind(final int bind) {
        this.bind = bind;
    }

    public boolean getState() {
        return state;
    }

    public void setState(final boolean state) {
        this.state = state;
    }
}

