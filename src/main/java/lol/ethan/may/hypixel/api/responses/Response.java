package lol.ethan.may.hypixel.api.responses;

import com.google.gson.JsonObject;

public class Response {
    private boolean success;
    private JsonObject raw;

    public boolean isSuccess() {
        return this.success;
    }

    public JsonObject getRaw() {
        return this.raw;
    }

    public void setRaw(JsonObject raw) {
        this.raw = raw;
    }
}

