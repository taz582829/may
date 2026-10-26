package lol.ethan.may.utils;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.Reader;

public class JsonUtils {
    private static final JsonParser PARSER = new JsonParser();
    public static Gson gson = new Gson();
    public static Gson GSON_PRETTY_PRINTING = new GsonBuilder().setPrettyPrinting().create();

    public static JsonElement parse(final String param) {
        return PARSER.parse(param);
    }

    public static JsonElement parse(final Reader param) {
        return PARSER.parse(param);
    }

    public static JsonElement parse(final JsonReader param) {
        return PARSER.parse(param);
    }

    public static String stringify(final JsonElement json) {
        return GSON_PRETTY_PRINTING.toJson(json);
    }

    public static int getInt(final JsonObject obj, final String key, final int defaultValue) {
        if (null == obj) {
            return defaultValue;
        }
        final String[] keys = key.split("\\.");
        JsonObject current = obj;
        for (int i = 0; i < keys.length - 1; i++) {
            if (null == current || !current.has(keys[i])) {
                return defaultValue;
            }
            current = current.getAsJsonObject(keys[i]);
        }
        if (null != current && current.has(keys[keys.length - 1])) {
            return current.get(keys[keys.length - 1]).getAsInt();
        }
        return defaultValue;
    }

    public static int getInt(final JsonObject obj, final String key) {
        return JsonUtils.getInt(obj, key, 0);
    }

    public static long getLong(final JsonObject obj, final String key, final long defaultValue) {
        if (null == obj) {
            return defaultValue;
        }
        final String[] keys = key.split("\\.");
        JsonObject current = obj;
        for (int i = 0; i < keys.length - 1; i++) {
            if (null == current || !current.has(keys[i])) {
                return defaultValue;
            }
            current = current.getAsJsonObject(keys[i]);
        }
        if (null != current && current.has(keys[keys.length - 1])) {
            return current.get(keys[keys.length - 1]).getAsLong();
        }
        return defaultValue;
    }

    public static long getLong(final JsonObject obj, final String key) {
        return JsonUtils.getLong(obj, key, 0L);
    }

    public static String getString(final JsonObject obj, final String key, final String defaultValue) {
        if (null == obj) {
            return defaultValue;
        }
        final String[] keys = key.split("\\.");
        JsonObject current = obj;
        for (int i = 0; i < keys.length - 1; i++) {
            if (null == current || !current.has(keys[i])) {
                return defaultValue;
            }
            current = current.getAsJsonObject(keys[i]);
        }
        if (null != current && current.has(keys[keys.length - 1])) {
            return current.get(keys[keys.length - 1]).getAsString();
        }
        return defaultValue;
    }

    public static JsonObject getObject(final JsonObject obj, final String key, final JsonObject defaultValue) {
        if (null == obj) {
            return defaultValue;
        }
        final String[] keys = key.split("\\.");
        JsonObject current = obj;
        for (int i = 0; i < keys.length - 1; i++) {
            if (null == current || !current.has(keys[i])) {
                return defaultValue;
            }
            current = current.getAsJsonObject(keys[i]);
        }
        if (null != current && current.has(keys[keys.length - 1])) {
            return current.get(keys[keys.length - 1]).getAsJsonObject();
        }
        return defaultValue;
    }

    public static JsonArray getArray(final JsonObject obj, final String key, final JsonArray defaultValue) {
        if (null == obj) {
            return defaultValue;
        }
        final String[] keys = key.split("\\.");
        JsonObject current = obj;
        for (int i = 0; i < keys.length - 1; i++) {
            if (null == current || !current.has(keys[i])) {
                return defaultValue;
            }
            current = current.getAsJsonObject(keys[i]);
        }
        if (null != current && current.has(keys[keys.length - 1])) {
            return current.get(keys[keys.length - 1]).getAsJsonArray();
        }
        return defaultValue;
    }

    public static JsonElement getElement(final JsonObject obj, final String key, final JsonElement defaultValue) {
        if (null == obj) {
            return defaultValue;
        }
        final String[] keys = key.split("\\.");
        JsonObject current = obj;
        for (int i = 0; i < keys.length - 1; i++) {
            if (null == current || !current.has(keys[i])) {
                return defaultValue;
            }
            current = current.getAsJsonObject(keys[i]);
        }
        if (null != current && current.has(keys[keys.length - 1])) {
            return current.get(keys[keys.length - 1]);
        }
        return defaultValue;
    }
}

