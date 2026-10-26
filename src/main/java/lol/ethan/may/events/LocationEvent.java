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

    public LocationEvent(String server, ServerType serverType, GameType gameType, String lobbyName, String mode, String map) {
        this.server = server;
        this.serverType = serverType;
        this.gameType = gameType;
        this.lobbyName = lobbyName;
        this.mode = mode;
        this.map = map;
    }

    public String getServer() {
        return this.server;
    }

    public ServerType getServerType() {
        return this.serverType;
    }

    public GameType getGameType() {
        return this.gameType;
    }

    public String getLobbyName() {
        return this.lobbyName;
    }

    public String getMode() {
        return this.mode;
    }

    public String getMap() {
        return this.map;
    }

    public boolean isLobby() {
        return this.lobbyName != null;
    }
}

