package lol.ethan.may.events;

import net.hypixel.data.type.GameType;
import net.hypixel.data.type.ServerType;

public class LocationEvent extends Event {
    private final String server;
    private final GameType gameType;
    private final ServerType serverType;
    private final String lobbyName;
    private final String mode;
    private final String map;

    public LocationEvent(final String server, final ServerType serverType, final GameType gameType, final String lobbyName, final String mode, final String map) {
        this.server = server;
        this.serverType = serverType;
        this.gameType = gameType;
        this.lobbyName = lobbyName;
        this.mode = mode;
        this.map = map;
    }

    public String getServer() {
        return server;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public GameType getGameType() {
        return gameType;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public String getMode() {
        return mode;
    }

    public String getMap() {
        return map;
    }

    public boolean isLobby() {
        return null != this.lobbyName;
    }
}

