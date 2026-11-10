package lol.ethan.may.commands;

import com.mojang.authlib.GameProfile;
import lol.ethan.may.Entry;
import lol.ethan.may.config.Config;
import lol.ethan.may.utils.ChatUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.StringUtils;

public class DebugCommand extends Command {
    public DebugCommand() {
        super("mdebug", "dbg");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length == 0) {
            Config.setDebugLogging(!Config.isDebugLogging());
            Entry.chat(Entry.PREFIX + "Debug Logging: " + (Config.isDebugLogging() ? "§aOn" : "§cOff"));
            return;
        }
        final String arg1 = args[0].trim().toLowerCase();
        if ("teams".equalsIgnoreCase(arg1)) {
            final StringBuilder builder = new StringBuilder("§7" + ChatUtils.getChatBreak() + "\n");
            for (final ScorePlayerTeam team : mc.theWorld.getScoreboard().getTeams()) {
                final String displayName = team.getColorPrefix() + team.getColorSuffix();
                if (args.length >= 2) {
                    final String arg2 = args[1];
                    if ("filter".equalsIgnoreCase(arg2)) {
                        if (isTeamValid(team)) {
                            builder.append(Entry.SHORT_PREFIX)
                                    .append(team.getTeamName())
                                    .append(" §f(")
                                    .append(displayName)
                                    .append("§f): Players (")
                                    .append(team.getMembershipCollection().size())
                                    .append("): ")
                                    .append(String.join("§r, ", team.getMembershipCollection()))
                                    .append("\n");
                        }
                        continue;
                    }

                }
                builder.append(Entry.SHORT_PREFIX)
                        .append(team.getTeamName())
                        .append(" §f(")
                        .append(displayName)
                        .append("§f): Players (")
                        .append(team.getMembershipCollection().size())
                        .append("): ")
                        .append(String.join("§r, ", team.getMembershipCollection()))
                        .append("\n");
            }
            builder.append("§7").append(ChatUtils.getChatBreak());
            Entry.chat(builder);
        } else if ("players".equalsIgnoreCase(arg1)) {
            final StringBuilder builder = new StringBuilder("§7" + ChatUtils.getChatBreak() + "\n");
            for (final NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                final GameProfile profile = playerInfo.getGameProfile();
                if (args.length >= 2) {
                    final String arg2 = args[1];
                    if ("filter".equalsIgnoreCase(arg2)) {
                        if (profile.getId().version() != 2) {
                            builder.append(Entry.SHORT_PREFIX)
                                    .append(profile.getName())
                                    .append(" §f(")
                                    .append(playerInfo.getDisplayName() == null ? profile.getName() : playerInfo.getDisplayName().getFormattedText())
                                    .append("§f): UUID: ")
                                    .append(profile.getId())
                                    .append("\n");
                        }
                        continue;
                    }

                }
                builder.append(Entry.SHORT_PREFIX)
                        .append(profile.getName())
                        .append(" §f(")
                        .append(playerInfo.getDisplayName() == null ? profile.getName() : playerInfo.getDisplayName().getFormattedText())
                        .append("§f): UUID: ")
                        .append(profile.getId())
                        .append(")")
                        .append("\n");
            }
            builder.append("§7").append(ChatUtils.getChatBreak());
            Entry.chat(builder);
        }
    }

    private boolean isTeamValid(final ScorePlayerTeam team) {
        boolean result = false;
        for (final String player : team.getMembershipCollection()) {
            if (!StringUtils.stripControlCodes(player).trim().isEmpty()) result = true;
        }
        return result;
    }
}
