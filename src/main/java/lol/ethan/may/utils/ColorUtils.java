package lol.ethan.may.utils;

import net.minecraft.scoreboard.ScorePlayerTeam;

import java.awt.*;

public class ColorUtils {

    public static char getLastColorCode(String text) {
        for (int i = text.length() - 1; i >= 0; --i) {
            char format;
            int index;
            if (text.charAt(i) != '§' || i + 1 >= text.length() || (index = "0123456789abcdefklmnorABCDEFKLMNOR".indexOf(format = text.charAt(i + 1))) == -1)
                continue;
            return index < 22 ? format : "0123456789abcdefklmnorABCDEFKLMNOR".charAt(index - 12);
        }
        return '\u0000';
    }

    public static Color setAlpha(Color color, int newAlpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), newAlpha);
    }

    public static int setAlpha(int color, int newAlpha) {
        return color & 0xFFFF0000 | (newAlpha & 0xFF) << 24;
    }

    public static String colorToHex(String color) {
        String c;
        String string = c = color.length() == 1 ? color : String.valueOf(color.charAt(1));
        if (c.equalsIgnoreCase("0")) {
            return "000000";
        }
        if (c.equalsIgnoreCase("1")) {
            return "0000AA";
        }
        if (c.equalsIgnoreCase("2")) {
            return "00AA00";
        }
        if (c.equalsIgnoreCase("3")) {
            return "00AAAA";
        }
        if (c.equalsIgnoreCase("4")) {
            return "AA0000";
        }
        if (c.equalsIgnoreCase("5")) {
            return "AA00AA";
        }
        if (c.equalsIgnoreCase("6")) {
            return "FFAA00";
        }
        if (c.equalsIgnoreCase("7")) {
            return "AAAAAA";
        }
        if (c.equalsIgnoreCase("8")) {
            return "555555";
        }
        if (c.equalsIgnoreCase("9")) {
            return "5555FF";
        }
        if (c.equalsIgnoreCase("a")) {
            return "55FF55";
        }
        if (c.equalsIgnoreCase("b")) {
            return "55FFFF";
        }
        if (c.equalsIgnoreCase("c")) {
            return "FF5555";
        }
        if (c.equalsIgnoreCase("d")) {
            return "FF55FF";
        }
        if (c.equalsIgnoreCase("e")) {
            return "FFFF55";
        }
        if (c.equalsIgnoreCase("f")) {
            return "FFFFFF";
        }
        return "FFFFFF";
    }

    public static String getColorFromTeam(ScorePlayerTeam team) {
        if (team == null) {
            return "FFFFFF";
        }
        String c = String.valueOf(ColorUtils.getLastColorCode(team.getColorPrefix()));
        return ColorUtils.colorToHex(c);
    }
}

