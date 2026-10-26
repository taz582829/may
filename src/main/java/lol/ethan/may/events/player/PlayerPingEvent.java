package lol.ethan.may.events.player;

import lol.ethan.may.events.Event;
import net.minecraft.client.network.NetworkPlayerInfo;

public class PlayerPingEvent extends Event {
    private final NetworkPlayerInfo playerInfo;
    private int ping;

    public PlayerPingEvent(int ping, NetworkPlayerInfo playerInfo) {
        this.ping = ping;
        this.playerInfo = playerInfo;
    }

    public int getPing() {
        return this.ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public NetworkPlayerInfo getPlayerInfo() {
        return this.playerInfo;
    }
}

