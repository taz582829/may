package lol.ethan.may.hypixel.api.other;

import lol.ethan.may.utils.HttpUtils;
import lol.ethan.may.utils.JsonUtils;

import java.util.concurrent.CompletableFuture;

public class NameHistory {

    public static CompletableFuture<NameHistoryResponse> getNameHistory(String uuid) {
        return HttpUtils.get("https://api.leeism.com/v2/player/" + uuid + "/names").thenApply(r -> JsonUtils.gson.fromJson(r, NameHistoryResponse.class));
    }
}

