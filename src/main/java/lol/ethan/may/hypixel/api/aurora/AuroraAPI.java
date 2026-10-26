package lol.ethan.may.hypixel.api.aurora;

import lol.ethan.may.utils.HttpUtils;
import lol.ethan.may.utils.JsonUtils;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AuroraAPI {
    public static CompletableFuture<PingResponse> getPing(final UUID uuid) {
        return HttpUtils.get("https://api.bordic.xyz/v3/player/ping?uuid=" + uuid).thenApply(res -> JsonUtils.gson.fromJson(res, PingResponse.class));
    }
    public static CompletableFuture<WinstreakResponse> getWinstreak(final UUID uuid) {
        return HttpUtils.get("https://api.bordic.xyz/v3/player/ping?uuid=" + uuid).thenApply(res -> JsonUtils.gson.fromJson(res, WinstreakResponse.class));
    }
}
