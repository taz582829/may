package lol.ethan.may.hypixel.modapi;

import com.google.common.base.Objects;
import lol.ethan.may.Entry;
import lol.ethan.may.events.LocationEvent;
import lol.ethan.may.hypixel.HypixelUtils;
import lol.ethan.may.utils.JsonUtils;
import net.hypixel.data.type.GameType;
import net.hypixel.data.type.ServerType;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.minecraft.util.StringUtils;
import lol.ethan.may.events.ChatEvent;
import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.SubscribeEvent;

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
            server = packet.getServerName();
            serverType = packet.getServerType().orElse(null);
            gameType = serverType == null ? null : GameType.valueOf(serverType.name());
            lobbyName = packet.getLobbyName().orElse(null);
            mode = packet.getMode().orElse(null);
            map = packet.getMap().orElse(null);
            EventBus.postEvent(new LocationEvent(server, serverType, gameType, lobbyName, mode, map));
        });
    }

    public static String getServer() {
        return INSTANCE == null ? null : INSTANCE.server;
    }

    public static GameType getGameType() {
        return INSTANCE == null ? null : INSTANCE.gameType;
    }

    public static String getLobbyName() {
        return INSTANCE == null ? null : INSTANCE.lobbyName;
    }

    public static String getMode() {
        return INSTANCE == null ? null : INSTANCE.mode;
    }

    public static String getMap() {
        return INSTANCE == null ? null : INSTANCE.map;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("server", server)
                .add("gameType", gameType)
                .add("serverType", serverType)
                .add("lobbyName", lobbyName)
                .add("mode", mode)
                .add("map", map)
                .toString();
    }
}
