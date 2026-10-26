package lol.ethan.may.utils;

import java.time.*;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MayStringUtils {

    public static String capitalizeText(String text) {
        Stream<String> stream = Arrays.stream(text.split("[\\s_]")).map(s -> String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1));
        return String.join(" ", stream.collect(Collectors.toList()));
    }

    public static String getTimeSince(long timestamp, long timeToCompare, ZoneId timezone) {
        String ago;
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), timezone);
        LocalDateTime timeNow = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeToCompare), timezone);
        Period period = Period.between(ldt.toLocalDate(), timeNow.toLocalDate());
        if (period.getYears() > 0) {
            boolean isPlural = period.getYears() == 1;
            ago = period.getYears() + (isPlural ? " year" : " years") + " ago";
        } else if (period.getMonths() > 0) {
            boolean isPlural = period.getMonths() == 1;
            ago = period.getMonths() + (isPlural ? " month" : " months") + " ago";
        } else if (period.getDays() > 0) {
            boolean isPlural = period.getDays() == 1;
            ago = period.getDays() + (isPlural ? " day" : " days") + " ago";
        } else {
            Duration duration = Duration.ofMillis(timeToCompare - timestamp);
            if (duration.toHours() > 0L) {
                boolean isPlural = duration.toHours() == 1L;
                ago = duration.toHours() + (isPlural ? " hour" : " hours") + " ago";
            } else if (duration.toMinutes() > 0L) {
                boolean isPlural = duration.toMinutes() == 1L;
                ago = duration.toMinutes() + (isPlural ? " minute" : " minutes") + " ago";
            } else if (duration.getSeconds() > 0L) {
                boolean isPlural = duration.getSeconds() == 1L;
                ago = duration.getSeconds() + (isPlural ? " second" : " seconds") + " ago";
            } else {
                ago = "just now";
            }
        }
        return ago;
    }

    public static String getTimeSince(long timestamp, long timeToCompare) {
        return MayStringUtils.getTimeSince(timestamp, timeToCompare, ZoneId.systemDefault());
    }

    public static String cleanText(String text) {
        return text.replaceAll("[^a-zA-Z0-9\\s:\\\\/§!@#$%^&*()\\-=+_{};\"'<>?]", "");
    }
}

