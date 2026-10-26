package lol.ethan.may.hypixel.stats;

import lol.ethan.may.hypixel.api.responses.PlayerResponse;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.List;

public interface IStats {
    IStats buildStats(PlayerResponse.Player player, String mode);
    List<String> getDisplayStats(NetworkPlayerInfo playerInfo);
    int getPing();
}
