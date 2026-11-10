package lol.ethan.may.rbw;

import com.mojang.authlib.GameProfile;
import lol.ethan.may.Entry;
import lol.ethan.may.config.Config;
import lol.ethan.may.events.ChatEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.WorldEvent;
import lol.ethan.may.events.player.RenderPlayerInTabEvent;
import lol.ethan.may.rbw.api.Lifetime;
import lol.ethan.may.rbw.api.Player;
import lol.ethan.may.rbw.api.RankedResponse;
import lol.ethan.may.utils.HttpUtils;
import lol.ethan.may.utils.JsonUtils;
import lol.ethan.may.utils.TimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RBWOverlay {
    private final Map<UUID, RankedResponse> statsMap = new HashMap<>();
    final Minecraft mc = Minecraft.getMinecraft();
    @SubscribeEvent
    public void onChat(final ChatEvent.Received event) {
        final String msg = event.getUnformattedMessage().trim();
        if ((msg.startsWith("Enemy Team: ") || msg.startsWith("Sending you to ") || "won the vote!".equals(msg) || msg.startsWith("Allowed All Game:")) && isOnRanked()) {
            TimeUtils.setTickDelay(() -> {
                for (final NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                    final GameProfile profile = playerInfo.getGameProfile();
                    if (profile.getId().version() == 4) {
                        HttpUtils.get("https://compmc.net/api/stats/profile?name=" + profile.getName()).thenAccept(r -> {
                            final RankedResponse stats = JsonUtils.gson.fromJson(r, RankedResponse.class);
                            statsMap.put(profile.getId(), stats);
                        });
                    }
                }
            }, 5);
        }
    }

    boolean isOnRanked() {
        return Entry.getServerIP().contains("compmc");
    }

    @SubscribeEvent
    public void onTabRender(final RenderPlayerInTabEvent event) {
        if (isOnRanked() && Config.rankedTabStats.enabled) {
            if (statsMap.containsKey(event.playerInfo.getGameProfile().getId())) {
                event.setText(getDisplayName(event.playerInfo, event.getText()));
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(final WorldEvent.Unload event) {
        statsMap.clear();
    }

    String getDisplayName(final NetworkPlayerInfo playerInfo, final String ogDisplayName) {
        final GameProfile profile = playerInfo.getGameProfile();
        final RankedResponse stats = statsMap.get(profile.getId());
        if (stats == null) return playerInfo.getDisplayName().getFormattedText();

        final StringBuilder builder = new StringBuilder();
        int hp = 0;
        final Scoreboard sb = mc.theWorld.getScoreboard();
        final ScoreObjective obj = sb.getObjectiveInDisplaySlot(2);
        if (obj != null && sb.entityHasObjective(profile.getName(), obj)) {
            final Score score = sb.getValueFromObjective(profile.getName(), obj);
            hp = score.getScorePoints();
        }
        final Player player = stats.getPlayer();
        final Lifetime lifetime = stats.getLifetime();

        final String kills = ogDisplayName.split(" ")[0];

        final int wins = lifetime.getWins();
        final double losses = lifetime.getLosses();
        final double wlr = losses == 0 ? wins : wins / losses;
        final int elo = stats.getSeason().getElo();

        builder.append("§8[")
                .append(getEloColor(elo))
                .append(elo)
                .append("§8] ")
                .append(playerInfo.getPlayerTeam().getColorPrefix());
        if (player.getNickname() == null) builder.append(player.getMinecraftName());
        else builder.append(player.getNickname()).append(" §7(").append(player.getMinecraftName()).append(")§r");

        builder.append(" ");
        builder.append("§e").append(getFormattedWLR(wlr));
        if (kills.contains("⚔")) builder.append(" §7| ").append(kills);
        builder.append(" §7| §c").append(hp).append("❤");

        return builder.toString();
    }

    public static String getFormattedWLR(final double wlr) {
        final String wlrText = String.format("%.2f", wlr);
        if (wlr < 0.5) return "§8" + wlrText;
        else if (wlr < 1) return "§7" + wlrText;
        else if (wlr < 2) return "§f" + wlrText;
        else if (wlr < 2.5) return "§a" + wlrText;
        else if (wlr < 4) return "§6" + wlrText;
        else if (wlr < 6) return "§c" + wlrText;
        return "§4" + wlrText;
    }

    public static EnumChatFormatting getEloColor(final int elo) {
        final int hundreds = elo / 100;
        switch (hundreds) {
            case 1:
                return EnumChatFormatting.WHITE;
            case 2:
                return EnumChatFormatting.GOLD;
            case 3:
                return EnumChatFormatting.AQUA;
            case 4:
                return EnumChatFormatting.DARK_GREEN;
            case 5:
                return EnumChatFormatting.DARK_AQUA;
            case 6:
                return EnumChatFormatting.DARK_RED;
            case 7:
                return EnumChatFormatting.LIGHT_PURPLE;
            case 8:
                return EnumChatFormatting.BLUE;
            case 9:
                return EnumChatFormatting.DARK_PURPLE;
            default:
                return EnumChatFormatting.DARK_GRAY;
        }
    }
}
