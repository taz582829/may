package lol.ethan.may.hypixel.stats;

import lol.ethan.may.hypixel.api.responses.PlayerResponse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class TabStats {
    protected static Minecraft mc = Minecraft.getMinecraft();
    private static final Map<UUID, IStats> statsMap = new HashMap<>();
    protected static Map<UUID, String> customDisplayNames = new HashMap<>();
    protected Map<UUID, IStats> getStatsMap() {
        return statsMap;
    }
    public void clear() {
        statsMap.clear();
        customDisplayNames.clear();
    }

    public abstract IStats createStats();
    public IStats refreshPlayer(final UUID uuid, final PlayerResponse.Player apiPlayer) {
        final IStats stats = statsMap.computeIfAbsent(uuid, id -> this.createStats());
        stats.buildStats(apiPlayer, null);
        statsMap.put(uuid, stats);
        return stats;
    }
    public IStats refreshPlayer(final UUID uuid, final PlayerResponse.Player apiPlayer, final String mode) {
        final IStats stats = statsMap.computeIfAbsent(uuid, id -> this.createStats());
        stats.buildStats(apiPlayer, mode);
        statsMap.put(uuid, stats);
        return stats;
    }
    public IStats getStats(final UUID uuid) {
        return statsMap.get(uuid);
    }
    protected abstract String getDisplayName(NetworkPlayerInfo playerInfo, IStats stats);
    public void setDisplayName(final NetworkPlayerInfo playerInfo, final String displayName) {
        customDisplayNames.put(playerInfo.getGameProfile().getId(), displayName);

    }
}
