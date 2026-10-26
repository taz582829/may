package lol.ethan.may.hypixel.modapi;

import com.google.common.base.Objects;
import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.LocationEvent;
import net.hypixel.data.type.GameType;
import net.hypixel.data.type.ServerType;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

/*
{"server":"dynamiclobby4D","gametype":"TNTGAMES","lobbyname":"tntlobby3"}
[15:15:46] [Client thread/INFO]: [CHAT]
[15:15:47] [Client thread/INFO]: [CHAT] {"server":"dynamiclobby18A","gametype":"SKYWARS","lobbyname":"swlobby4"}
[15:15:49] [Client thread/INFO]: [CHAT] Sending you to mini83DF!
[15:15:50] [Client thread/INFO]: [CHAT]
[15:15:50] [Client thread/INFO]: [CHAT] ethavn has joined (1/12)!
[15:15:51] [Client thread/INFO]: [CHAT] {"server":"mini83DF","gametype":"SKYWARS","mode":"solo_normal","map":"Aquacrown"}
 */
public class LocationData {
    private String server;
    private GameType gameType;
    private ServerType serverType;
    private String lobbyName;
    private String mode;
    private String map;

    public static LocationData INSTANCE;

    public LocationData() {
        INSTANCE = this;
        HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket.class);
        HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket.class, packet -> {
            this.server = packet.getServerName();
            this.serverType = packet.getServerType().orElse(null);
            this.gameType = null == serverType ? null : GameType.valueOf(this.serverType.name());
            this.lobbyName = packet.getLobbyName().orElse(null);
            this.mode = packet.getMode().orElse(null);
            this.map = packet.getMap().orElse(null);
            EventBus.postEvent(new LocationEvent(this.server, this.serverType, this.gameType, this.lobbyName, this.mode, this.map));
        });
    }

    public static String getServer() {
        return null == INSTANCE ? null : INSTANCE.server;
    }

    public static GameType getGameType() {
        return null == INSTANCE ? null : INSTANCE.gameType;
    }

    public static String getLobbyName() {
        return null == INSTANCE ? null : INSTANCE.lobbyName;
    }

    public static String getMode() {
        return null == INSTANCE ? null : INSTANCE.mode;
    }

    public static String getMap() {
        return null == INSTANCE ? null : INSTANCE.map;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("server", this.server)
                .add("gameType", this.gameType)
                .add("serverType", this.serverType)
                .add("lobbyName", this.lobbyName)
                .add("mode", this.mode)
                .add("map", this.map)
                .toString();
    }
}
