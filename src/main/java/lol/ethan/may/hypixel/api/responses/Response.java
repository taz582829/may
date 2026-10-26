package lol.ethan.may.hypixel.api.responses;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Response {
    private boolean success;
    private JsonObject raw;

    public boolean isSuccess() {
        return success;
    }

    public JsonObject getRaw() {
        return raw;
    }

    public void setRaw(JsonObject raw) {
        this.raw = raw;
    }
}
