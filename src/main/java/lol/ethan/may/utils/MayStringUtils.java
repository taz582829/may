package lol.ethan.may.utils;

import java.time.*;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MayStringUtils {

    public static String capitalizeText(final String text) {
        final Stream<String> stream = Arrays.stream(text.split("[\\s_]")).map(s -> String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1));
        return String.join(" ", stream.collect(Collectors.toList()));
    }

    public static String getTimeSince(final long timestamp, final long timeToCompare, final ZoneId timezone) {
        final String ago;
        final LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), timezone);
        final LocalDateTime timeNow = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeToCompare), timezone);
        final Period period = Period.between(ldt.toLocalDate(), timeNow.toLocalDate());
        if (period.getYears() > 0) {
            final boolean isPlural = period.getYears() == 1;
            ago = period.getYears() + (isPlural ? " year" : " years") + " ago";
        } else if (period.getMonths() > 0) {
            final boolean isPlural = period.getMonths() == 1;
            ago = period.getMonths() + (isPlural ? " month" : " months") + " ago";
        } else if (period.getDays() > 0) {
            final boolean isPlural = period.getDays() == 1;
            ago = period.getDays() + (isPlural ? " day" : " days") + " ago";
        } else {
            final Duration duration = Duration.ofMillis(timeToCompare - timestamp);
            if (duration.toHours() > 0L) {
                final boolean isPlural = duration.toHours() == 1L;
                ago = duration.toHours() + (isPlural ? " hour" : " hours") + " ago";
            } else if (duration.toMinutes() > 0L) {
                final boolean isPlural = duration.toMinutes() == 1L;
                ago = duration.toMinutes() + (isPlural ? " minute" : " minutes") + " ago";
            } else if (duration.getSeconds() > 0L) {
                final boolean isPlural = duration.getSeconds() == 1L;
                ago = duration.getSeconds() + (isPlural ? " second" : " seconds") + " ago";
            } else {
                ago = "just now";
            }
        }
        return ago;
    }

    public static String getTimeSince(final long timestamp, final long timeToCompare) {
        return MayStringUtils.getTimeSince(timestamp, timeToCompare, ZoneId.systemDefault());
    }

    public static String cleanText(final String text) {
        return text.replaceAll("[^a-zA-Z0-9\\s:\\\\/§!@#$%^&*()\\-=+_{};\"'<>?]", "");
    }
}

