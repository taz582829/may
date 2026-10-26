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

    public static CompletableFuture<PlayerResponse> getPlayer(UUID uuid) {
        CompletableFuture<PlayerResponse> future = new CompletableFuture<>();
        if (playerCache.has(uuid)) {
            future.complete(playerCache.get(uuid));
        } else {
            if (Config.hypixelKeyless.enabled) {
                HttpUtils.get("https://thirdpartycache.lunarclientprod.com/hypixel/ursa-minor/player/" + uuid).whenComplete((element, throwable) -> {
                    PlayerResponse response = gson.fromJson(element, PlayerResponse.class);
                    playerCache.put(uuid, response);

                    future.complete(response);
                });
            } else {
                HttpUtils.get("https://api.hypixel.net/v2/player?uuid=" + uuid + "&key=" + Config.hypixelKey.value).whenComplete((element, throwable) -> {
                    PlayerResponse response = gson.fromJson(element, PlayerResponse.class);
                    playerCache.put(uuid, response);

                    future.complete(response);
                });
            }

        }

        return future;
    }
    public static CompletableFuture<PlayerResponse> getPlayer(String name) {
        CompletableFuture<PlayerResponse> future = new CompletableFuture<>();
        MojangAPI.getUUID(name).thenAccept(id -> {
            String uuid = UUIDUtils.dashUUID(id);
            HypixelAPI.getPlayer(UUID.fromString(uuid)).thenAccept(future::complete);
        });
        return future;
    }
    public static String getDisplayName(PlayerResponse.Player player) {
        try {
            String ign = player.ign;
            String rankField = player.getRankField();
            if (rankField == null) {
                return "§7" + ign;
            }
            if (rankField.equals("prefix")) return player.prefix + " " + ign;

            String rank = player.getClass().getDeclaredField(rankField).get(player).toString();

            StringBuilder builder = new StringBuilder();
            if (rankField.equalsIgnoreCase("rank")) {
                if (rank.equalsIgnoreCase("admin") || rank.equalsIgnoreCase("game_master")) return "§c[§6ዞ§c] " + ign;
                else return "§c[§fYOUTUBE§c] " + ign;
            } else if (rankField.equalsIgnoreCase("monthlyPackageRank")) {
                boolean aquaPlus = player.monthlyRankColor != null && player.monthlyRankColor.equalsIgnoreCase("aqua");
                if (aquaPlus) {
                    builder.append("§b[MVP");
                } else builder.append("§6[MVP");

                EnumChatFormatting plusColor = EnumChatFormatting.getValueByName(player.rankPlusColor);
                if (plusColor == null) plusColor = EnumChatFormatting.RED;
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
                        EnumChatFormatting plusColor = EnumChatFormatting.getValueByName(player.rankPlusColor == null ? "RED" : player.rankPlusColor);
                        builder.append(plusColor).append("+").append("§b]");
                        break;
                }
                builder.append(" ").append(ign);

            } else builder.append("§7").append(ign);
            return builder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static double getNetworkLevel(long exp) {
        return (Math.sqrt(exp + 15312.5) - 125/ Math.sqrt(2))/(25*Math.sqrt(2));
    }
    public static String formatNetworkLevel(double nwl) {
        String color;
        if (nwl < 35) color = "§c";
        else if (nwl < 45) color = "§6";
        else if (nwl < 55) color = "§a";
        else if (nwl < 65) color = "§e";
        else if (nwl < 75) color = "§d";
        else if (nwl < 85) color = "§f";
        else if (nwl < 95) color = "§9";
        else if (nwl < 150) color = "§2";
        else if (nwl < 200) color = "§3";
        else if (nwl < 250) color = "§7";
        else color = "§0";
        return color + String.format("%.2f", nwl);
    }
    private static String formatBedwarsStarIcon(String scheme) {
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

    private static String formatBedwarsBrackets(String activeBrackets, String formattedStar) {
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
    public static String bedwarsLevelFormatted(int level, String starKey, String colorScheme, String activeBrackets) {

        int prestige = level / 100;
        int key = prestige * 100;
        int maxDefinedPrestige = PrestigeSchemes.SCHEMES.keySet().stream()
                .max(Integer::compareTo)
                .orElse(0);
        if (key > maxDefinedPrestige) key = maxDefinedPrestige;
        StringBuilder value = new StringBuilder();
        String starIcon = formatBedwarsStarIcon(starKey);
        String text = "[" + level + starIcon + "]";

        PrestigeScheme scheme = null;
        if (colorScheme.contains("random") || colorScheme.equalsIgnoreCase("default")) {
            scheme = PrestigeSchemes.SCHEMES.get(key);
        } else {
            for (Map.Entry<Integer, PrestigeScheme> s : PrestigeSchemes.SCHEMES.entrySet()) {
                if (colorScheme.equalsIgnoreCase(s.getValue().key)) {
                    scheme = s.getValue();
                    break;
                }
            }
            if (scheme == null) scheme = PrestigeSchemes.SCHEMES.get(key);
        }

        for (int i = 0; i < text.length(); i++) {
            value.append("§")
                    .append(scheme.colors.get(i % scheme.colors.size()))
                    .append(text.charAt(i));
        }
        return formatBedwarsBrackets(activeBrackets, value.toString());
    }
    public static String bedwarsLevelFormatted(int bwsLvl, JsonObject bedwarsStats) {
        return HypixelAPI.bedwarsLevelFormatted(
                bwsLvl,
                JsonUtils.getString(bedwarsStats, "active_star", "black_open"),
                JsonUtils.getString(bedwarsStats, "active_prestige_scheme", "prestige_scheme_none"),
                JsonUtils.getString(bedwarsStats, "active_prestige_bracket", "")
        );
    }
    public static String bedwarsLevelFormatted(int level, String starScheme) {
        return bedwarsLevelFormatted(level, starScheme, "default", "default");
    }
    public static boolean isAPIHidden(JsonObject stats) {
        if (stats == null) return false;
        JsonObject duelsStats = stats.getAsJsonObject("Duels");
        if (duelsStats != null && duelsStats.has("wins")) {
            int wins = duelsStats.get("wins").getAsInt();
            return wins > 1 && !duelsStats.has("best_overall_winstreak");
        }
        return false;
    }
    public static String fkdrFormatted(double number) {
        String n = formatter.format(number);

        if (number < 1) return "§7" + n;
        if (number < 3) return "§f" + n;
        if (number < 5) return "§a" + n;
        if (number < 7) return "§2" + n;
        if (number < 10) return "§e" + n;
        if (number < 20) return "§6" + n;
        if (number < 30) return "§4" + n;
        if (number < 50) return "§d" + n;
        return "§5" + n;
    }

    public static String winsFormatted(int number) {
        if (number < 150) {
            return "§7" + number;
        } else if (number < 300) {
            return "§f" + number;
        } else if (number < 450) {
            return "§a" + number;
        } else if (number < 1500) {
            return "§2" + number;
        } else if (number < 2250) {
            return "§e" + number;
        } else if (number < 4500) {
            return "§6" + number;
        } else if (number < 7500) {
            return "§c" + number;
        } else if (number < 15000) {
            return "§4" + number;
        } else if (number < 30000) {
            return "§d" + number;
        }
        return "§5" + number;


    }
    public static String finalsFormatted(int finals) {
        String color;
        if (finals < 500) {
            color = "§7"; // Gray
        } else if (finals < 1000) {
            color = "§f"; // White
        } else if (finals < 2500) {
            color = "§a"; // Green
        } else if (finals < 5000) {
            color = "§2"; // Dark Green
        } else if (finals < 7500) {
            color = "§e"; // Yellow
        } else if (finals < 15000) {
            color = "§6"; // Gold
        } else if (finals < 25000) {
            color = "§c"; // Red
        } else if (finals < 50000) {
            color = "§4"; // Dark Red
        } else if (finals < 100000) {
            color = "§d"; // Light Purple
        } else {
            color = "§5"; // Dark Purple
        }
        return color + finals;
    }
}
