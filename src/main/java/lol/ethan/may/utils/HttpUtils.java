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

    public static CompletableFuture<JsonElement> get(final String url) {
        return CompletableFuture.supplyAsync(() -> {
            try (final CloseableHttpClient client = HttpClients.createDefault();
                 final CloseableHttpResponse response = client.execute(new HttpGet(url))) {
                final HttpEntity entity = response.getEntity();
                if (null == entity) return null;
                final String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                return JsonUtils.parse(result);
            } catch (final Exception e) {
                throw new RuntimeException("Error sending GET request to " + url + ": \n" + e.getMessage());
            }
        });
    }

    @SafeVarargs
    public static CompletableFuture<JsonElement> get(final String url, final Consumer<HttpGet>... customizers) {
        return CompletableFuture.supplyAsync(() -> {
            final HttpGet httpGet = new HttpGet(url);
            for (final Consumer<HttpGet> customizer : customizers) {
                customizer.accept(httpGet);
            }
            try (final CloseableHttpClient client = HttpClients.createDefault();
                 final CloseableHttpResponse response = client.execute(httpGet)) {

                final HttpEntity entity = response.getEntity();
                if (null == entity) return null;
                final String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                return JsonUtils.parse(result);
            } catch (final Exception e) {
                final String headers = Arrays.stream(httpGet.getAllHeaders()).map(h -> h.getName() + ": " + h.getValue()).collect(Collectors.joining(", "));
                throw new RuntimeException("Error sending GET request to " + url + " (headers: " + headers + "):\n" + e.getMessage());
            }
        });
    }

    @SafeVarargs
    public static CompletableFuture<JsonElement> post(final String url, final JsonElement body, final Consumer<HttpPost>... customizers) {
        final HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Content-Type", "application/json");
        if (null != body) {
            httpPost.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));
        }
        for (final Consumer<HttpPost> customizer : customizers) {
            customizer.accept(httpPost);
        }

        return CompletableFuture.supplyAsync(() -> {
            try (final CloseableHttpClient client = HttpClients.createDefault();
                 final CloseableHttpResponse response = client.execute(httpPost)) {
                final HttpEntity entity = response.getEntity();
                if (null == entity) return null;
                return JsonUtils.parse(EntityUtils.toString(entity));
            } catch (final Exception e) {
                final String headers = Arrays.stream(httpPost.getAllHeaders()).map(h -> h.getName() + ": " + h.getValue()).collect(Collectors.joining(", "));
                final String bodyText = null != body ? body.toString() : "No body found";
                throw new RuntimeException("Error sending POST request to " + url + " (headers: " + headers + ", body: " + bodyText + "):\n" + e.getMessage());
            }

        });
    }

    @SafeVarargs
    public static CompletableFuture<JsonElement> postNoJSON(final String url, final JsonElement body, final Consumer<HttpPost>... customizers) {
        final HttpPost httpPost = new HttpPost(url);
        if (null != body) {
            httpPost.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));
        }
        for (final Consumer<HttpPost> customizer : customizers) {
            customizer.accept(httpPost);
        }

        return CompletableFuture.supplyAsync(() -> {
            try (final CloseableHttpClient client = HttpClients.createDefault();
                 final CloseableHttpResponse response = client.execute(httpPost)) {
                final HttpEntity entity = response.getEntity();
                if (null == entity) return null;
                return JsonUtils.parse(EntityUtils.toString(entity));
            } catch (final Exception e) {
                final String headers = Arrays.stream(httpPost.getAllHeaders()).map(h -> h.getName() + ": " + h.getValue()).collect(Collectors.joining(", "));
                final String bodyText = null != body ? body.toString() : "No body found";
                throw new RuntimeException("Error sending POST request to " + url + " (headers: " + headers + ", body: " + bodyText + "):\n" + e.getMessage());
            }

        });
    }
}
