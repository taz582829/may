package lol.ethan.may.hypixel.bedwars.stats;

import java.util.List;

public class PrestigeScheme {
    public List<String> colors;
    public int level;
    public String key;

    public PrestigeScheme(List<String> colors, int prestige, String key) {
        this.colors = colors;
        this.level = prestige;
        this.key = key;
    }
}