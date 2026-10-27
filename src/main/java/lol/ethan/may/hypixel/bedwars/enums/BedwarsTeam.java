package lol.ethan.may.hypixel.bedwars.enums;

import net.minecraft.util.StringUtils;

public enum BedwarsTeam {
    RED("§r§c§lR "),
    BLUE("§r§9§lB "),
    GREEN("§r§a§lG "),
    YELLOW("§r§e§lY "),
    AQUA("§r§b§lA "),
    WHITE("§r§f§lW "),
    PINK("§r§d§lP "),
    GRAY("§r§8§lS ");

    public final String prefix;
    public final String displayName;
    public final String formattedDisplayName;

    BedwarsTeam(final String prefix) {
        this.prefix = prefix;

        final String name = name();
        this.displayName = StringUtils.stripControlCodes(String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1).toLowerCase());

        this.formattedDisplayName = prefix.substring(0, 2) + this.displayName;
    }
}