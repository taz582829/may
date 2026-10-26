package lol.ethan.may.hypixel.modapi;

import com.google.common.base.Objects;
import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.LocationEvent;
import net.hypixel.data.type.GameType;
import net.hypixel.data.type.ServerType;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

public class LocationData {
    public static LocationData INSTANCE;
    private String server;
    private GameType gameType;
    private ServerType serverType;
    private String lobbyName;
    private String mode;
    private String map;

    public LocationData() {
        INSTANCE = this;
        HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket.class);
        HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket.class, packet -> {
            this.server = packet.getServerName();
            this.serverType = packet.getServerType().orElse(null);
            this.gameType = this.serverType == null ? null : GameType.valueOf(this.serverType.name());
            this.lobbyName = packet.getLobbyName().orElse(null);
            this.mode = packet.getMode().orElse(null);
            this.map = packet.getMap().orElse(null);
            EventBus.postEvent(new LocationEvent(this.server, this.serverType, this.gameType, this.lobbyName, this.mode, this.map));
        });
    }

    public static String getServer() {
        return INSTANCE == null ? null : LocationData.INSTANCE.server;
    }

    public static GameType getGameType() {
        return INSTANCE == null ? null : LocationData.INSTANCE.gameType;
    }

    public static String getLobbyName() {
        return INSTANCE == null ? null : LocationData.INSTANCE.lobbyName;
    }

    public static String getMode() {
        return INSTANCE == null ? null : LocationData.INSTANCE.mode;
    }

    public static String getMap() {
        return INSTANCE == null ? null : LocationData.INSTANCE.map;
    }

    public String toString() {
        return Objects.toStringHelper(this).add("server", this.server).add("gameType", this.gameType).add("serverType", this.serverType).add("lobbyName", this.lobbyName).add("mode", this.mode).add("map", this.map).toString();
    }
}

