package lol.ethan.may.hypixel.api;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lol.ethan.may.hypixel.api.responses.Response;

public class ResponseTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!Response.class.isAssignableFrom(type.getRawType())) {
            return null;
        }

        TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) {
                delegate.write(out, value);
            }

            @Override
            public T read(JsonReader in) {
                JsonElement json = JsonParser.parseReader(in);

                T response = delegate.fromJsonTree(json);

                ((Response) response).setRaw(json.getAsJsonObject());

                return response;
            }
        };
    }
}
