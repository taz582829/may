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
    private int joinCount = 0;
    private int leaveCount = 0;

    private boolean shouldPrint(int count) {
        int maxPartySize;
        if (BedwarsUtils.currentMode == BedwarsUtils.Mode.SOLO) {
            return false;
        }
        switch (BedwarsUtils.currentMode) {
            case DOUBLES: {
                maxPartySize = 2;
                break;
            }
            case THREES: {
                maxPartySize = 3;
                break;
            }
            case TWO_FOUR:
            case FOURS: {
                maxPartySize = 4;
                break;
            }
            default: {
                return false;
            }
        }
        return count >= 2 && count <= maxPartySize;
    }

    @SubscribeEvent
    public void onChat(ChatEvent.Received event) {
        if (LocationData.getLobbyName() != null || LocationData.getGameType() != GameType.BEDWARS) {
            return;
        }
        if (BedwarsUtils.isInBedwars()) {
            return;
        }
        String msg = event.getUnformattedMessage().trim();
        if (msg.matches("(\\w{2,16}) has joined \\(\\d+/\\d+\\)!")) {
            ++this.joinCount;
            if (this.joinTimeout != null) {
                this.joinTimeout.cancel();
            }
            this.joinTimeout = TimeUtils.setTimeout(() -> {
                if (this.shouldPrint(this.joinCount)) {
                    Entry.chat("§dMay §8» §rParty of §a" + this.joinCount + " §fjoined.");
                }
                this.joinCount = 0;
            }, 400);
        } else if (msg.matches("\\w{2,16} has quit!")) {
            ++this.leaveCount;
            if (this.leaveTimeout != null) {
                this.leaveTimeout.cancel();
            }
            this.leaveTimeout = TimeUtils.setTimeout(() -> {
                if (this.shouldPrint(this.joinCount)) {
                    Entry.chat("§dMay §8» §rParty of §c" + this.joinCount + " §fleft.");
                }
                this.joinCount = 0;
            }, 400);
        }
    }
}

