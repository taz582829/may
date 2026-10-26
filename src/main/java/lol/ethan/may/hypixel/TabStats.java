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
    private volatile boolean dirty;

    public TabStats(final String... cols) {
        this.columns.addAll(Arrays.asList(cols));
    }
    public void register(final UUID id, final String... stats) {
        this.rows.put(id, Arrays.asList(stats));
        this.dirty = true;
    }
    public String get(final UUID id) {
        if (this.dirty) this.buildStats();
        return this.aligned.get(id);
    }
    private void buildStats() {
        this.dirty = false;
        final FontRenderer fr = mc.fontRendererObj;
        final Map<UUID, List<String>> rowMap = new HashMap<>(this.rows);

        int cols = 0;
        for (final List<String> r : rowMap.values()) cols = Math.max(cols, r.size());
        final int[] max = new int[cols];
        for (final List<String> stats : rowMap.values()) {
            for (int i = 0; i < stats.size(); i++) {
                max[i] = Math.max(max[i], fr.getStringWidth(stats.get(i)));
            }
        }

        final Map<UUID, String> output = new HashMap<>();
        for (final Map.Entry<UUID, List<String>> entry : rowMap.entrySet()) {
            final List<String> row = entry.getValue();
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < row.size(); i++) {
                final boolean last = i == row.size() - 1;
                builder.append(last ? row.get(i) : pad(row.get(i), max[i] + this.spaceWidth));
            }
            output.put(entry.getKey(), builder.toString());
        }
        this.aligned = output;
    }
    private static String pad(final String text, final int width) {
        final FontRenderer fr = mc.fontRendererObj;
        final int diff = width - fr.getStringWidth(text);
        if (0 >= diff) return text;

        int b = 0;
        while (4 > b && 0 != (diff - 5 * b) % 4) b++;
        int a = (diff - 5 * b) / 4;
        if (0 > a) {
            a = diff / 4;
            b = 0;
        }

        final StringBuilder sb = new StringBuilder(text);
        for (int i = 0; i < a; i++) sb.append(' ');
        if (0 < b) {
            sb.append("§l");
            for (int i = 0; i < b; i++) sb.append(' ');
            sb.append("§r");
        }
        return sb.toString();
    }
    public int getDisplayNameWidth() {
        int width = 0;
        for (final String display : this.columns) {
            width += mc.fontRendererObj.getStringWidth(display) + this.spaceWidth;
        }
        return width;
    }
    public String getDisplayName() {
        return String.join(" ", this.columns);
    }
}
