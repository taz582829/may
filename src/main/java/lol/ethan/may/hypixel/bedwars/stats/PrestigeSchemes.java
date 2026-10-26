package lol.ethan.may.hypixel.bedwars.stats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lol.ethan.may.Entry;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PrestigeSchemes {
    public static Map<Integer, PrestigeScheme> SCHEMES = new HashMap<>();

    static {
        try (InputStream stream = Entry.getResourceAsStream("assets/may/bedwars/bedwars_stars.json")) {
            JsonObject root = new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonObject schemes = root.getAsJsonObject("prestige_schemes");
            for (Map.Entry entry : schemes.entrySet()) {
                JsonObject value = ((JsonElement) entry.getValue()).getAsJsonObject();
                JsonArray colorArray = value.getAsJsonArray("colors");
                int prestige = value.get("prestige").getAsInt();
                ArrayList<String> colors = new ArrayList<>();
                for (JsonElement color : colorArray) {
                    colors.add(color.getAsString());
                }
                PrestigeScheme scheme = new PrestigeScheme(colors, prestige, (String) entry.getKey());
                SCHEMES.put(prestige, scheme);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

