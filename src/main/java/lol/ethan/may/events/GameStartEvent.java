package lol.ethan.may.events;

public class GameStartEvent extends Event {
    private final Mode mode;

    public GameStartEvent(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return this.mode;
    }

    public enum Mode {
        BEDWARS,
        SKYWARS,
        DUELS,
        BW_DUELS,
        TNT_TAG

    }
}

