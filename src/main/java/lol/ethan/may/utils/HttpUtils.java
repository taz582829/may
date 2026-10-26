package lol.ethan.may.utils;

import com.google.gson.JsonElement;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HttpUtils {

    public static CompletableFuture<JsonElement> get(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(new HttpGet(url))) {
                HttpEntity entity = response.getEntity();
                if (entity == null) return null;
                String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                return JsonUtils.parse(result);
            } catch (Exception e) {
                throw new RuntimeException("Error sending GET request to " + url + ": \n" + e.getMessage());
            }
        });
    }

    @SafeVarargs
    public static CompletableFuture<JsonElement> get(String url, Consumer<HttpGet>... customizers) {
        return CompletableFuture.supplyAsync(() -> {
            final HttpGet httpGet = new HttpGet(url);
            for (Consumer<HttpGet> customizer : customizers) {
                customizer.accept(httpGet);
            }
            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(httpGet)) {

                HttpEntity entity = response.getEntity();
                if (entity == null) return null;
                String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                return JsonUtils.parse(result);
            } catch (Exception e) {
                String headers = Arrays.stream(httpGet.getAllHeaders()).map(h -> h.getName() + ": " + h.getValue()).collect(Collectors.joining(", "));
                throw new RuntimeException("Error sending GET request to " + url + " (headers: " + headers + "):\n" + e.getMessage());
            }
        });
    }

    @SafeVarargs
    public static CompletableFuture<JsonElement> post(String url, JsonElement body, Consumer<HttpPost>... customizers) {
        final HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Content-Type", "application/json");
        if (body != null) {
            httpPost.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));
        }
        for (Consumer<HttpPost> customizer : customizers) {
            customizer.accept(httpPost);
        }

        return CompletableFuture.supplyAsync(() -> {
            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                if (entity == null) return null;
                return JsonUtils.parse(EntityUtils.toString(entity));
            } catch (Exception e) {
                String headers = Arrays.stream(httpPost.getAllHeaders()).map(h -> h.getName() + ": " + h.getValue()).collect(Collectors.joining(", "));
                String bodyText = body != null ? body.toString() : "No body found";
                throw new RuntimeException("Error sending POST request to " + url + " (headers: " + headers + ", body: " + bodyText + "):\n" + e.getMessage());
            }

        });
    }

    @SafeVarargs
    public static CompletableFuture<JsonElement> postNoJSON(String url, JsonElement body, Consumer<HttpPost>... customizers) {
        final HttpPost httpPost = new HttpPost(url);
        if (body != null) {
            httpPost.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));
        }
        for (Consumer<HttpPost> customizer : customizers) {
            customizer.accept(httpPost);
        }

        return CompletableFuture.supplyAsync(() -> {
            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                if (entity == null) return null;
                return JsonUtils.parse(EntityUtils.toString(entity));
            } catch (Exception e) {
                String headers = Arrays.stream(httpPost.getAllHeaders()).map(h -> h.getName() + ": " + h.getValue()).collect(Collectors.joining(", "));
                String bodyText = body != null ? body.toString() : "No body found";
                throw new RuntimeException("Error sending POST request to " + url + " (headers: " + headers + ", body: " + bodyText + "):\n" + e.getMessage());
            }

        });
    }
}
