package lol.ethan.may.utils;

import net.minecraft.scoreboard.ScorePlayerTeam;

import java.awt.*;

public class ColorUtils {

    public static char getLastColorCode(final String text) {
        for (int i = text.length() - 1; i >= 0; --i) {
            final char format;
            final int index;
            if (text.charAt(i) != '§' || i + 1 >= text.length() || (index = "0123456789abcdefklmnorABCDEFKLMNOR".indexOf(format = text.charAt(i + 1))) == -1)
                continue;
            return index < 22 ? format : "0123456789abcdefklmnorABCDEFKLMNOR".charAt(index - 12);
        }
        return '\u0000';
    }

    public static Color setAlpha(final Color color, final int newAlpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), newAlpha);
    }

    public static int setAlpha(final int color, final int newAlpha) {
        return color & 0xFFFF0000 | (newAlpha & 0xFF) << 24;
    }

    public static String colorToHex(final String color) {
        final String c;
        final String string = c = color.length() == 1 ? color : String.valueOf(color.charAt(1));
        if ("0".equalsIgnoreCase(c)) {
            return "000000";
        }
        if ("1".equalsIgnoreCase(c)) {
            return "0000AA";
        }
        if ("2".equalsIgnoreCase(c)) {
            return "00AA00";
        }
        if ("3".equalsIgnoreCase(c)) {
            return "00AAAA";
        }
        if ("4".equalsIgnoreCase(c)) {
            return "AA0000";
        }
        if ("5".equalsIgnoreCase(c)) {
            return "AA00AA";
        }
        if ("6".equalsIgnoreCase(c)) {
            return "FFAA00";
        }
        if ("7".equalsIgnoreCase(c)) {
            return "AAAAAA";
        }
        if ("8".equalsIgnoreCase(c)) {
            return "555555";
        }
        if ("9".equalsIgnoreCase(c)) {
            return "5555FF";
        }
        if ("a".equalsIgnoreCase(c)) {
            return "55FF55";
        }
        if ("b".equalsIgnoreCase(c)) {
            return "55FFFF";
        }
        if ("c".equalsIgnoreCase(c)) {
            return "FF5555";
        }
        if ("d".equalsIgnoreCase(c)) {
            return "FF55FF";
        }
        if ("e".equalsIgnoreCase(c)) {
            return "FFFF55";
        }
        if ("f".equalsIgnoreCase(c)) {
            return "FFFFFF";
        }
        return "FFFFFF";
    }

    public static String getColorFromTeam(final ScorePlayerTeam team) {
        if (team == null) {
            return "FFFFFF";
        }
        final String c = String.valueOf(ColorUtils.getLastColorCode(team.getColorPrefix()));
        return ColorUtils.colorToHex(c);
    }
}

