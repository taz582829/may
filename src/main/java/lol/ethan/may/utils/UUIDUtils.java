package lol.ethan.may.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UUIDUtils {
    public static final Pattern UUID_REGEX = Pattern.compile("(\\w{8})-?(\\w{4})-?(\\w{4})-?(\\w{4})-?(\\w{12})");

    public static String dashUUID(final String uuid) {
        final Matcher match = UUID_REGEX.matcher(uuid);
        if (!match.matches()) {
            return uuid;
        }
        return match.replaceAll("$1-$2-$3-$4-$5");
    }
}

