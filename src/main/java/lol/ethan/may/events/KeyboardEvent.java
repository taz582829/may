package lol.ethan.may.events;

public class KeyboardEvent extends Event {
    private int bind;
    private boolean state;

    public KeyboardEvent(int bind, boolean state) {
        this.bind = bind;
        this.state = state;
    }

    public int getBind() {
        return this.bind;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public boolean getState() {
        return this.state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}

