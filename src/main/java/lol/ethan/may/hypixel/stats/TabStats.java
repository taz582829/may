package lol.ethan.may.hypixel.stats;

import lol.ethan.may.hypixel.Denicker;
import lol.ethan.may.hypixel.api.responses.PlayerResponse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.lang.reflect.Field;
import java.util.*;

public abstract class TabStats {
    protected static Minecraft mc = Minecraft.getMinecraft();
    private static Map<UUID, IStats> statsMap = new HashMap<>();
    protected static Map<UUID, String> customDisplayNames = new HashMap<>();
    protected Map<UUID, IStats> getStatsMap() {
        return statsMap;
    }
    public void clear() {
        statsMap.clear();
        customDisplayNames.clear();
    }

    public abstract IStats createStats();
    public IStats refreshPlayer(UUID uuid, PlayerResponse.Player apiPlayer) {
        IStats stats = statsMap.computeIfAbsent(uuid, id -> createStats());
        stats.buildStats(apiPlayer, null);
        statsMap.put(uuid, stats);
        return stats;
    }
    public IStats refreshPlayer(UUID uuid, PlayerResponse.Player apiPlayer, String mode) {
        IStats stats = statsMap.computeIfAbsent(uuid, id -> createStats());
        stats.buildStats(apiPlayer, mode);
        statsMap.put(uuid, stats);
        return stats;
    }
    public IStats getStats(UUID uuid) {
        return statsMap.get(uuid);
    }
    protected abstract String getDisplayName(NetworkPlayerInfo playerInfo, IStats stats);
    public void setDisplayName(NetworkPlayerInfo playerInfo, String displayName) {
        customDisplayNames.put(playerInfo.getGameProfile().getId(), displayName);

    }
}
