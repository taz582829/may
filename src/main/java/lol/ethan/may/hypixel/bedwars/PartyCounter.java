package lol.ethan.may.hypixel.bedwars;

import lol.ethan.may.Entry;
import lol.ethan.may.events.ChatEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.hypixel.modapi.LocationData;
import lol.ethan.may.utils.TimeUtils;
import net.hypixel.data.type.GameType;

import java.util.Timer;

public class PartyCounter {
    private Timer joinTimeout;
    private Timer leaveTimeout;
    private int joinCount;
    private int leaveCount;

    private boolean shouldPrint(final int count) {
        if (BedwarsUtils.Mode.SOLO == BedwarsUtils.currentMode) return false;
        final int maxPartySize;
        switch (BedwarsUtils.currentMode) {
            case DOUBLES:
                maxPartySize = 2;
                break;
            case THREES:
                maxPartySize = 3;
                break;
            case TWO_FOUR:
            case FOURS:
                maxPartySize = 4;
                break;
            default: return false;


        }
        return 2 <= count && count <= maxPartySize;
    }

    @SubscribeEvent
    public void onChat(final ChatEvent.Received event) {
        if (null != LocationData.getLobbyName() || GameType.BEDWARS != LocationData.getGameType()) return;
        if (BedwarsUtils.isInBedwars()) return;
        final String msg = event.getMessage().getUnformattedText().trim();
        if (msg.matches("(\\w{2,16}) has joined \\(\\d+/\\d+\\)!")) {
            this.joinCount++;
            if (null != joinTimeout) this.joinTimeout.cancel();
            this.joinTimeout = TimeUtils.setTimeout(() -> {
                if (this.shouldPrint(this.joinCount)) {
                    Entry.chat(Entry.PREFIX + "Party of §a" + this.joinCount + " §fjoined.");
                }
                this.joinCount = 0;
            }, 400);
        } else if (msg.matches("\\w{2,16} has quit!")) {
            this.leaveCount++;
            if (null != leaveTimeout) this.leaveTimeout.cancel();
            this.leaveTimeout = TimeUtils.setTimeout(() -> {
                if (this.shouldPrint(this.joinCount)) {
                    Entry.chat(Entry.PREFIX + "Party of §c" + this.joinCount + " §fleft.");
                }
                this.joinCount = 0;
            }, 400);
        }

    }
}
