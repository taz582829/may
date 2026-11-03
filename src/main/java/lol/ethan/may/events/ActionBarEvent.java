package lol.ethan.may.events;

public class ActionBarEvent extends Event {
    private String message;

    public ActionBarEvent(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
