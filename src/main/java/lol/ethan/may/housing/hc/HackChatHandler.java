package lol.ethan.may.housing.hc;

import com.google.gson.JsonObject;
import com.neovisionaries.ws.client.*;
import lol.ethan.may.Entry;
import lol.ethan.may.events.user.ClientDisconnectFromServerEvent;
import lol.ethan.may.utils.JsonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;
import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HackChatHandler {
    private static String nick = "ethan";
    private static final Set<String> onlineSet = new HashSet<>();
    public static boolean toggled = false;
    public static void setNick(final String nick) {
        HackChatHandler.nick = nick;
    }

    public static Set<String> getOnlineSet() {
        return onlineSet;
    }

    public static String getNick() {
        return nick;
    }

    public static class Response {
        public String cmd;
        public String nick;
        public String text;
        public String color;
        public List<String> nicks;
    }
    public static WebSocket HACK_CHAT_WS;
    static Executor executor = Executors.newSingleThreadExecutor();
    static Minecraft mc = Minecraft.getMinecraft();

    public static CompletableFuture<Void> connect() {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            HACK_CHAT_WS = new WebSocketFactory()
                    .createSocket("wss://hack.chat/chat-ws");

            HACK_CHAT_WS.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(final WebSocket websocket, final String message) {
                    final Response response = JsonUtils.gson.fromJson(message, Response.class);

                    if (Objects.equals(response.cmd, "chat") && response.nick != null) {
                        final HackChatEvent.Chat event = new HackChatEvent.Chat(response.nick, response.text, response.color);
                        Entry.chat(event.buildMessage());
                        EventBus.postEvent(event);
                    } else if (response.cmd.equals("onlineAdd")) {
                        final HackChatEvent.OnlineAdd event = new HackChatEvent.OnlineAdd(response.nick);
                        Entry.chat(event.buildMessage());
                        EventBus.postEvent(event);
                    } else if (response.cmd.equals("onlineRemove")) {
                        final HackChatEvent.OnlineRemove event = new HackChatEvent.OnlineRemove(response.nick);
                        Entry.chat(event.buildMessage());
                        EventBus.postEvent(event);
                    } else if (response.cmd.equals("info")) {
                        final HackChatEvent.Info event = new HackChatEvent.Info(response.text);
                        Entry.chat(event.buildMessage());
                        EventBus.postEvent(event);
                    } else if (response.cmd.equals("onlineSet")) {
                        final HackChatEvent.OnlineSet event = new HackChatEvent.OnlineSet(response.nicks);
                        onlineSet.clear();
                        onlineSet.addAll(response.nicks);
                        Entry.chat(event.buildMessage());
                        EventBus.postEvent(event);
                    }
                }

                @Override
                public void onConnected(final WebSocket websocket, final Map<String, List<String>> headers) throws Exception {
                    future.complete(null);
                    super.onConnected(websocket, headers);
                }
            });

            HACK_CHAT_WS.connect();
        } catch (final WebSocketException | IOException e) {
            throw new RuntimeException(e);
        }
        return future;
    }
    public static void disconnect() {
        if (HACK_CHAT_WS != null) {
            HACK_CHAT_WS.disconnect();
            HACK_CHAT_WS = null;
        }
    }
    public static void joinChannel(final String channelId) {
        executor.execute(() -> {
            if (HACK_CHAT_WS != null) {
                final JsonObject body = new JsonObject();
                body.addProperty("cmd", "join");
                body.addProperty("channel", channelId);
                body.addProperty("nick", nick);
                HACK_CHAT_WS.sendText(JsonUtils.gson.toJson(body));
            } else {
                connect().thenAccept(v -> {
                    joinChannel(channelId);
                });
            }
        });
    }
    public static void joinChannel(final String channelId, final String nick) {
        HackChatHandler.nick = nick;
        executor.execute(() -> {
            if (HACK_CHAT_WS != null) {
                final JsonObject body = new JsonObject();
                body.addProperty("cmd", "join");
                body.addProperty("channel", channelId);
                body.addProperty("nick", nick);
                HACK_CHAT_WS.sendText(JsonUtils.gson.toJson(body));
            } else {
                connect().thenAccept(v -> {
                    joinChannel(channelId);
                });
            }
        });
    }
    public static void sendMessage(final String msg) {
        final JsonObject body = new JsonObject();
        body.addProperty("cmd", "chat");
        body.addProperty("text", msg);
        body.addProperty("nick", nick);
        HACK_CHAT_WS.sendText(JsonUtils.gson.toJson(body));
    }
    public static void sendMessage(final String msg, final String nick) {
        final JsonObject body = new JsonObject();
        body.addProperty("cmd", "chat");
        body.addProperty("text", msg);
        body.addProperty("nick", nick);
        HACK_CHAT_WS.sendText(JsonUtils.gson.toJson(body));
    }

    @SubscribeEvent
    public void onPacket(final PacketEvent.Send event) {
        if (event.getPacket() instanceof C01PacketChatMessage) {
            final C01PacketChatMessage packet = (C01PacketChatMessage) event.getPacket();
            if (packet.getMessage().startsWith("/ac ") && toggled) return;
            if (packet.getMessage().startsWith(">")) {
                event.setCancelled(true);
                if (HACK_CHAT_WS == null) {
                    Entry.chat("§cYou are not connected to a HackChat server!");
                    return;
                }
                final String msg = packet.getMessage().substring(">".length());
                if (msg.isEmpty()) {
                    toggled = !toggled;
                    Entry.chat("§7You are " + (toggled ? "now" : "no longer") + " sending all messages to §aHackChat");
                } else {
                    sendMessage(msg);
                }
            } else if (toggled && !packet.getMessage().startsWith("/")) {
                sendMessage(packet.getMessage());
                event.setCancelled(true);
            }
        }
    }
    @SubscribeEvent
    public void onClientDisconnect(final ClientDisconnectFromServerEvent event) {
        disconnect();
        toggled = false;
    }
}
