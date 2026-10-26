package lol.ethan.may.hypixel.bedwars.stats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lol.ethan.may.Entry;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrestigeSchemes {
    public static Map<Integer, PrestigeScheme> SCHEMES = new HashMap<>();
    static {
        try (final InputStream stream = Entry.class.getClassLoader().getResourceAsStream("assets/frost/bedwars/bedwars_stars.json")) {
            if (null == stream) throw new RuntimeException("couldn't find bedwars stars in assets!");

            final JsonObject root = new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8))
                    .getAsJsonObject();
            final JsonObject schemes = root.getAsJsonObject("prestige_schemes");

            for (final Map.Entry<String, JsonElement> entry : schemes.entrySet()) {
                final JsonObject value = entry.getValue().getAsJsonObject();

                final JsonArray colorArray = value.getAsJsonArray("colors");
                final int prestige = value.get("prestige").getAsInt();

                final List<String> colors = new ArrayList<>();

                for (final JsonElement color : colorArray) {
                    colors.add(color.getAsString());
                }
                final PrestigeScheme scheme = new PrestigeScheme(
                        colors,
                        prestige,
                        entry.getKey()
                );

                SCHEMES.put(prestige, scheme);
            }


        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

    }
}
