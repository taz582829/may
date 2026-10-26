package lol.ethan.may.hypixel.api.trick;

import lol.ethan.may.utils.HttpUtils;
import lol.ethan.may.utils.JsonUtils;

import java.util.concurrent.CompletableFuture;

public class TrickAPI {
    public static CompletableFuture<TrickDenickResponse> denick(boolean solo, String... info) {

        StringBuilder args = new StringBuilder();
        for (int i = 0; i < info.length; i++) {
            String part = info[i];
            if (i == 0) {
                args.append("?");
            } else {
                args.append("&");
            }

            args.append(part);
        }
        if (solo) args.append("&mode=1s");

        return HttpUtils.get("https://trickapi.duckdns.org/api/bw-denick" + args, c -> c.setHeader("API-Key", "8a6319fd46ee4b89d8101fbd83401f3ce3dd75e3"))
                .thenApply(r -> {
                    System.out.println(r);
                    return JsonUtils.gson.fromJson(r, TrickDenickResponse.class);
                });
    }
}
