package lol.ethan.may.hypixel.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lol.ethan.may.config.Config;
import lol.ethan.may.hypixel.api.responses.PlayerResponse;
import lol.ethan.may.hypixel.bedwars.stats.PrestigeScheme;
import lol.ethan.may.hypixel.bedwars.stats.PrestigeSchemes;
import lol.ethan.may.utils.*;
import net.minecraft.util.EnumChatFormatting;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HypixelAPI {
    private static final DecimalFormat formatter = new DecimalFormat("#.##");

    public static Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new ResponseTypeAdapterFactory())
            .create();

    private static final Cache<UUID, PlayerResponse> playerCache = new Cache<>();

    public static CompletableFuture<PlayerResponse> getPlayer(final UUID uuid) {
        final CompletableFuture<PlayerResponse> future = new CompletableFuture<>();
        if (playerCache.has(uuid)) {
            future.complete(playerCache.get(uuid));
        } else {
            if (Config.hypixelKeyless.enabled) {
                HttpUtils.get("https://thirdpartycache.lunarclientprod.com/hypixel/ursa-minor/player/" + uuid).whenComplete((element, throwable) -> {
                    final PlayerResponse response = gson.fromJson(element, PlayerResponse.class);
                    playerCache.put(uuid, response);

                    future.complete(response);
                });
            } else {
                HttpUtils.get("https://api.hypixel.net/v2/player?uuid=" + uuid + "&key=" + Config.hypixelKey.value).whenComplete((element, throwable) -> {
                    final PlayerResponse response = gson.fromJson(element, PlayerResponse.class);
                    playerCache.put(uuid, response);

                    future.complete(response);
                });
            }

        }

        return future;
    }
    public static CompletableFuture<PlayerResponse> getPlayer(final String name) {
        final CompletableFuture<PlayerResponse> future = new CompletableFuture<>();
        MojangAPI.getUUID(name).thenAccept(id -> {
            final String uuid = UUIDUtils.dashUUID(id);
            HypixelAPI.getPlayer(UUID.fromString(uuid)).thenAccept(future::complete);
        });
        return future;
    }
    public static String getDisplayName(final PlayerResponse.Player player) {
        try {
            final String ign = player.ign;
            final String rankField = player.getRankField();
            if (null == rankField) {
                return "§7" + ign;
            }
            if (rankField.equals("prefix")) return player.prefix + " " + ign;

            final String rank = player.getClass().getDeclaredField(rankField).get(player).toString();

            final StringBuilder builder = new StringBuilder();
            if (rankField.equalsIgnoreCase("rank")) {
                if (rank.equalsIgnoreCase("admin") || rank.equalsIgnoreCase("game_master")) return "§c[§6ዞ§c] " + ign;
                else return "§c[§fYOUTUBE§c] " + ign;
            } else if (rankField.equalsIgnoreCase("monthlyPackageRank")) {
                final boolean aquaPlus = "aqua".equalsIgnoreCase(player.monthlyRankColor);
                if (aquaPlus) {
                    builder.append("§b[MVP");
                } else builder.append("§6[MVP");

                EnumChatFormatting plusColor = EnumChatFormatting.getValueByName(player.rankPlusColor);
                if (null == plusColor) plusColor = EnumChatFormatting.RED;
                builder.append(plusColor).append("++").append(aquaPlus ? "§b]" : "§6]").append(" ").append(ign);
            } else if (rankField.equalsIgnoreCase("newPackageRank") || rankField.equalsIgnoreCase("packageRank")) {
                switch (rank) {
                    case "VIP":
                        builder.append("§a[VIP]");
                        break;
                    case "VIP_PLUS":
                        builder.append("§a[VIP§6+§a]");
                        break;
                    case "MVP":
                        builder.append("§b[MVP]");
                        break;
                    case "MVP_PLUS":
                        builder.append("§b[MVP");
                        final EnumChatFormatting plusColor = EnumChatFormatting.getValueByName(null == player.rankPlusColor ? "RED" : player.rankPlusColor);
                        builder.append(plusColor).append("+").append("§b]");
                        break;
                }
                builder.append(" ").append(ign);

            } else builder.append("§7").append(ign);
            return builder.toString();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static double getNetworkLevel(final long exp) {
        return (Math.sqrt(exp + 15312.5) - 125/ Math.sqrt(2))/(25*Math.sqrt(2));
    }
    public static String formatNetworkLevel(final double nwl) {
        final String color;
        if (35 > nwl) color = "§c";
        else if (45 > nwl) color = "§6";
        else if (55 > nwl) color = "§a";
        else if (65 > nwl) color = "§e";
        else if (75 > nwl) color = "§d";
        else if (85 > nwl) color = "§f";
        else if (95 > nwl) color = "§9";
        else if (150 > nwl) color = "§2";
        else if (200 > nwl) color = "§3";
        else if (250 > nwl) color = "§7";
        else color = "§0";
        return color + String.format("%.2f", nwl);
    }
    private static String formatBedwarsStarIcon(final String scheme) {
        switch (scheme) {
            case "star_black_outlined":
                return "✭";
            case "star_four_clubs":
                return "✥";
            case "star_white_outlined":
                return "⚝";
            case "star_white_circled":
                return "✪";
            case "star_nautical":
                return "✯";
            case "star_pinwheel":
                return "✵";
            case "star_hollow":
                return "✰";
            case "star_four_pointed":
                return "✦";
            default:
                return "✫";
        }
    }

    private static String formatBedwarsBrackets(final String activeBrackets, final String formattedStar) {
        switch (activeBrackets) {
            case "prestige_bracket_angled":
                return formattedStar.replace("[", "<").replace("]", ">");
            case "prestige_bracket_double_angle":
                return formattedStar.replace("[", "«").replace("]", "»");
            case "prestige_bracket_parentheses":
                return formattedStar.replace("[", "(").replace("]", ")");
            case "prestige_bracket_curly":
                return formattedStar.replace("[", "{").replace("]", "}");
            default: return formattedStar;
        }
    }
    public static String bedwarsLevelFormatted(final int level, final String starKey, final String colorScheme, final String activeBrackets) {

        final int prestige = level / 100;
        int key = prestige * 100;
        final int maxDefinedPrestige = PrestigeSchemes.SCHEMES.keySet().stream()
                .max(Integer::compareTo)
                .orElse(0);
        if (key > maxDefinedPrestige) key = maxDefinedPrestige;
        final StringBuilder value = new StringBuilder();
        final String starIcon = formatBedwarsStarIcon(starKey);
        final String text = "[" + level + starIcon + "]";

        PrestigeScheme scheme = null;
        if (colorScheme.contains("random") || colorScheme.equalsIgnoreCase("default")) {
            scheme = PrestigeSchemes.SCHEMES.get(key);
        } else {
            for (final Map.Entry<Integer, PrestigeScheme> s : PrestigeSchemes.SCHEMES.entrySet()) {
                if (colorScheme.equalsIgnoreCase(s.getValue().key)) {
                    scheme = s.getValue();
                    break;
                }
            }
            if (null == scheme) scheme = PrestigeSchemes.SCHEMES.get(key);
        }

        for (int i = 0; i < text.length(); i++) {
            value.append("§")
                    .append(scheme.colors.get(i % scheme.colors.size()))
                    .append(text.charAt(i));
        }
        return formatBedwarsBrackets(activeBrackets, value.toString());
    }
    public static String bedwarsLevelFormatted(final int bwsLvl, final JsonObject bedwarsStats) {
        return HypixelAPI.bedwarsLevelFormatted(
                bwsLvl,
                JsonUtils.getString(bedwarsStats, "active_star", "black_open"),
                JsonUtils.getString(bedwarsStats, "active_prestige_scheme", "prestige_scheme_none"),
                JsonUtils.getString(bedwarsStats, "active_prestige_bracket", "")
        );
    }
    public static String bedwarsLevelFormatted(final int level, final String starScheme) {
        return bedwarsLevelFormatted(level, starScheme, "default", "default");
    }
    public static boolean isAPIHidden(final JsonObject stats) {
        if (null == stats) return false;
        final JsonObject duelsStats = stats.getAsJsonObject("Duels");
        if (null != duelsStats && duelsStats.has("wins")) {
            final int wins = duelsStats.get("wins").getAsInt();
            return 1 < wins && !duelsStats.has("best_overall_winstreak");
        }
        return false;
    }
    public static String fkdrFormatted(final double number) {
        final String n = formatter.format(number);

        if (1 > number) return "§7" + n;
        if (3 > number) return "§f" + n;
        if (5 > number) return "§a" + n;
        if (7 > number) return "§2" + n;
        if (10 > number) return "§e" + n;
        if (20 > number) return "§6" + n;
        if (30 > number) return "§4" + n;
        if (50 > number) return "§d" + n;
        return "§5" + n;
    }

    public static String winsFormatted(final int number) {
        if (150 > number) {
            return "§7" + number;
        } else if (300 > number) {
            return "§f" + number;
        } else if (450 > number) {
            return "§a" + number;
        } else if (1500 > number) {
            return "§2" + number;
        } else if (2250 > number) {
            return "§e" + number;
        } else if (4500 > number) {
            return "§6" + number;
        } else if (7500 > number) {
            return "§c" + number;
        } else if (15000 > number) {
            return "§4" + number;
        } else if (30000 > number) {
            return "§d" + number;
        }
        return "§5" + number;


    }
    public static String finalsFormatted(final int finals) {
        final String color;
        if (500 > finals) {
            color = "§7"; // Gray
        } else if (1000 > finals) {
            color = "§f"; // White
        } else if (2500 > finals) {
            color = "§a"; // Green
        } else if (5000 > finals) {
            color = "§2"; // Dark Green
        } else if (7500 > finals) {
            color = "§e"; // Yellow
        } else if (15000 > finals) {
            color = "§6"; // Gold
        } else if (25000 > finals) {
            color = "§c"; // Red
        } else if (50000 > finals) {
            color = "§4"; // Dark Red
        } else if (100000 > finals) {
            color = "§d"; // Light Purple
        } else {
            color = "§5"; // Dark Purple
        }
        return color + finals;
    }
}
