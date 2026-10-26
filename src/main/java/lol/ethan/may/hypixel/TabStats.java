package lol.ethan.may.hypixel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TabStats {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public List<String> columns = new ArrayList<>();
    private volatile Map<UUID, String> aligned = Collections.emptyMap();
    public int spaceWidth = mc.fontRendererObj.getStringWidth(" ");
    private final Map<UUID, List<String>> rows = new ConcurrentHashMap<>();
    private volatile boolean dirty = false;

    public TabStats(String... cols) {
        columns.addAll(Arrays.asList(cols));
    }
    public void register(UUID id, String... stats) {
        rows.put(id, Arrays.asList(stats));
        dirty = true;
    }
    public String get(UUID id) {
        if (dirty) buildStats();
        return aligned.get(id);
    }
    private void buildStats() {
        dirty = false;
        FontRenderer fr = mc.fontRendererObj;
        Map<UUID, List<String>> rowMap = new HashMap<>(rows);

        int cols = 0;
        for (List<String> r : rowMap.values()) cols = Math.max(cols, r.size());
        int[] max = new int[cols];
        for (List<String> stats : rowMap.values()) {
            for (int i = 0; i < stats.size(); i++) {
                max[i] = Math.max(max[i], fr.getStringWidth(stats.get(i)));
            }
        }

        Map<UUID, String> output = new HashMap<>();
        for (Map.Entry<UUID, List<String>> entry : rowMap.entrySet()) {
            List<String> row = entry.getValue();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < row.size(); i++) {
                boolean last = i == row.size() - 1;
                builder.append(last ? row.get(i) : pad(row.get(i), max[i] + spaceWidth));
            }
            output.put(entry.getKey(), builder.toString());
        }
        aligned = output;
    }
    private static String pad(String text, int width) {
        FontRenderer fr = mc.fontRendererObj;
        int diff = width - fr.getStringWidth(text);
        if (diff <= 0) return text;

        int b = 0;
        while (b < 4 && (diff - 5 * b) % 4 != 0) b++;
        int a = (diff - 5 * b) / 4;
        if (a < 0) {
            a = diff / 4;
            b = 0;
        }

        StringBuilder sb = new StringBuilder(text);
        for (int i = 0; i < a; i++) sb.append(' ');
        if (b > 0) {
            sb.append("§l");
            for (int i = 0; i < b; i++) sb.append(' ');
            sb.append("§r");
        }
        return sb.toString();
    }
    public int getDisplayNameWidth() {
        int width = 0;
        for (String display : columns) {
            width += mc.fontRendererObj.getStringWidth(display) + spaceWidth;
        }
        return width;
    }
    public String getDisplayName() {
        return String.join(" ", columns);
    }
}
