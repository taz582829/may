package lol.ethan.may.hypixel.duels;

import lol.ethan.may.render.huds.BasicHud;
import net.minecraft.scoreboard.ScorePlayerTeam;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DuelsRankHUD extends BasicHud {
    public DuelsRankHUD() {
        super(true, 200, 200);
    }

    @Override
    public boolean shouldShow() {
        return super.shouldShow() &&
                DuelsUtils.isInDuels() &&
                null != mc.theWorld &&
                null != mc.theWorld.getScoreboard() &&
                DuelsUtils.isInPregame();
    }

    @Override
    protected List<String> getLines(final boolean example) {
        final List<String> result = new ArrayList<>();
        if (null == mc.theWorld || null == mc.theWorld.getScoreboard()) return Collections.emptyList();
        for (final ScorePlayerTeam team : this.mc.theWorld.getScoreboard().getTeams()) {
            if (0 == team.getMembershipCollection().size()) continue;
            final String prefix = team.getColorPrefix();
            if (null == prefix) continue;
            if (prefix.startsWith("§k")) {
                final String color = prefix.substring(0, 2);
                final String rank = this.getRankFromColor(color);
                result.add(rank);
            }
        }
        if (0 < result.size()) result.add(0, "§f§lPregame Ranks");
        return result;
    }

    private String getRankFromColor(@NotNull final String teamColor) {
        switch (teamColor) {
            case "§7": return "§7Non";
            case "§a": return "§aVIP§f/§aVIP§6+";
            case "§b": return "§bMVP§f/§bMVP§c+";
            case "§6": return "§6MVP§c++";
            case "§c": return "§cYoutube§f/§cStaff";
            default: return "§cUnknown";
        }
    }
}
