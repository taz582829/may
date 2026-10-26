package lol.ethan.may.utils;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.Reader;

public class JsonUtils {
    private static final JsonParser PARSER = new JsonParser();
    public static Gson gson = new Gson();
    public static Gson GSON_PRETTY_PRINTING = new GsonBuilder().setPrettyPrinting().create();

    public static JsonElement parse(String param) {
        return PARSER.parse(param);
    }

    public static JsonElement parse(Reader param) {
        return PARSER.parse(param);
    }

    public static JsonElement parse(JsonReader param) {
        return PARSER.parse(param);
    }

    public static String stringify(JsonElement json) {
        return GSON_PRETTY_PRINTING.toJson(json);
    }

    public static int getInt(JsonObject obj, String key, int defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        String[] keys = key.split("\\.");
        JsonObject current = obj;
        for (int i = 0; i < keys.length - 1; ++i) {
            if (current == null || !current.has(keys[i])) {
                return defaultValue;
            }
            current = current.getAsJsonObject(keys[i]);
        }
        if (current != null && current.has(keys[keys.length - 1])) {
            return current.get(keys[keys.length - 1]).getAsInt();
        }
        return defaultValue;
    }

    public static int getInt(JsonObject obj, String key) {
        return JsonUtils.getInt(obj, key, 0);
    }

    public static long getLong(JsonObject obj, String key, long defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        String[] keys = key.split("\\.");
        JsonObject current = obj;
        for (int i = 0; i < keys.length - 1; ++i) {
            if (current == null || !current.has(keys[i])) {
                return defaultValue;
            }
            current = current.getAsJsonObject(keys[i]);
        }
        if (current != null && current.has(keys[keys.length - 1])) {
            return current.get(keys[keys.length - 1]).getAsLong();
        }
        return defaultValue;
    }

    public static long getLong(JsonObject obj, String key) {
        return JsonUtils.getLong(obj, key, 0L);
    }

    public static String getString(JsonObject obj, String key, String defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        String[] keys = key.split("\\.");
        JsonObject current = obj;
        for (int i = 0; i < keys.length - 1; ++i) {
            if (current == null || !current.has(keys[i])) {
                return defaultValue;
            }
            current = current.getAsJsonObject(keys[i]);
        }
        if (current != null && current.has(keys[keys.length - 1])) {
            return current.get(keys[keys.length - 1]).getAsString();
        }
        return defaultValue;
    }

    public static JsonObject getObject(JsonObject obj, String key, JsonObject defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        String[] keys = key.split("\\.");
        JsonObject current = obj;
        for (int i = 0; i < keys.length - 1; ++i) {
            if (current == null || !current.has(keys[i])) {
                return defaultValue;
            }
            current = current.getAsJsonObject(keys[i]);
        }
        if (current != null && current.has(keys[keys.length - 1])) {
            return current.get(keys[keys.length - 1]).getAsJsonObject();
        }
        return defaultValue;
    }

    public static JsonArray getArray(JsonObject obj, String key, JsonArray defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        String[] keys = key.split("\\.");
        JsonObject current = obj;
        for (int i = 0; i < keys.length - 1; ++i) {
            if (current == null || !current.has(keys[i])) {
                return defaultValue;
            }
            current = current.getAsJsonObject(keys[i]);
        }
        if (current != null && current.has(keys[keys.length - 1])) {
            return current.get(keys[keys.length - 1]).getAsJsonArray();
        }
        return defaultValue;
    }

    public static JsonElement getElement(JsonObject obj, String key, JsonElement defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        String[] keys = key.split("\\.");
        JsonObject current = obj;
        for (int i = 0; i < keys.length - 1; ++i) {
            if (current == null || !current.has(keys[i])) {
                return defaultValue;
            }
            current = current.getAsJsonObject(keys[i]);
        }
        if (current != null && current.has(keys[keys.length - 1])) {
            return current.get(keys[keys.length - 1]);
        }
        return defaultValue;
    }
}

