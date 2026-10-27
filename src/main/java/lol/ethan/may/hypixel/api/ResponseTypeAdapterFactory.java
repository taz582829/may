package lol.ethan.may.hypixel.api;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lol.ethan.may.hypixel.api.responses.Response;

public class ResponseTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
        if (!Response.class.isAssignableFrom(type.getRawType())) {
            return null;
        }

        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        return new TypeAdapter<T>() {
            @Override
            public void write(final JsonWriter out, final T value) {
                delegate.write(out, value);
            }

            @Override
            public T read(final JsonReader in) {
                final JsonElement json = JsonParser.parseReader(in);

                final T response = delegate.fromJsonTree(json);

                ((Response) response).setRaw(json.getAsJsonObject());

                return response;
            }
        };
    }
}