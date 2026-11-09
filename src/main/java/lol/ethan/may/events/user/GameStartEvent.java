package lol.ethan.may.events.user;

import lol.ethan.may.events.Event;

public class GameStartEvent extends Event {
    private final Mode mode;

    public GameStartEvent(final Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public enum Mode {
        BEDWARS,
        SKYWARS,
        DUELS,
        BW_DUELS,
        TNT_TAG

    }
}

