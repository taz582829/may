package lol.ethan.may.utils;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MojangAPI {

    public static CompletableFuture<List<ShortProfile>> bulkRequest(final List<String> igns) {
        final List<CompletableFuture<Void>> futures = new ArrayList<>();
        final List<ShortProfile> profiles = Collections.synchronizedList(new ArrayList<>());

        final int batchSize = 10;
        for (int i = 0; i < igns.size(); i += batchSize) {
            final int end = Math.min(i + batchSize, igns.size());

            final List<String> sublist = igns.subList(i, end);
            final JsonArray arr = new JsonArray();
            for (final String ign : sublist) {
                arr.add(ign);
            }
            final CompletableFuture<Void> future = HttpUtils.post("https://api.mojang.com/profiles/minecraft", arr)
                    .thenAccept((res) -> {
                        if (!res.isJsonArray()) return;
                        final JsonArray responseArray = res.getAsJsonArray();
                        final ShortProfile profile = JsonUtils.gson.fromJson(responseArray, ShortProfile.class);
                        profiles.add(profile);
                    });
            futures.add(future);
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> profiles);
    }

    public static CompletableFuture<Profile> getProfile(final String identifier) {
        return HttpUtils.get("https://mowojang.seraph.si/" + identifier).thenApply(r -> JsonUtils.gson.fromJson(r, Profile.class));

    }

    public static CompletableFuture<String> getIGN(final String uuid) {
        return getProfile(uuid).thenApply(p -> p.name);
    }

    public static CompletableFuture<String> getUUID(final String ign) {
        return getProfile(ign).thenApply(p -> p.id);
    }

    public static class SkinProfile {
        @SerializedName("profileName")
        public String profileName;

        @SerializedName("profileId")
        public String profileId;

        @SerializedName("textures")
        public Textures textures;
    }

    public static class Textures {
        @SerializedName("SKIN")
        public Skin skin;
    }

    public static class Skin {
        @SerializedName("url")
        public String url;
    }

    public static class Profile {
        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
        @SerializedName("properties")
        public List<String> properties;

        public String getDashedUUID() {
            return UUIDUtils.dashUUID(this.id);
        }
    }

    public static class ShortProfile {
        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
    }
}