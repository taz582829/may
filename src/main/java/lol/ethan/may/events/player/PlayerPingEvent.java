package lol.ethan.may.events.player;

import lol.ethan.may.events.Event;
import net.minecraft.client.network.NetworkPlayerInfo;

public class PlayerPingEvent extends Event {
    private final NetworkPlayerInfo playerInfo;
    private int ping;

    public PlayerPingEvent(final int ping, final NetworkPlayerInfo playerInfo) {
        this.ping = ping;
        this.playerInfo = playerInfo;
    }

    public int getPing() {
        return ping;
    }

    public void setPing(final int ping) {
        this.ping = ping;
    }

    public NetworkPlayerInfo getPlayerInfo() {
        return playerInfo;
    }
}

