package lol.ethan.may.hypixel.bedwars.stats;

import java.util.List;

public class PrestigeScheme {
    public List<String> colors;
    public int level;
    public String key;

    public PrestigeScheme(final List<String> colors, final int prestige, final String key) {
        this.colors = colors;
        level = prestige;
        this.key = key;
    }
}