package lol.ethan.may.hypixel.api.luna;/*
package lol.ethan.may.hypixel.api.luna;

import io.socket.client.IO;
import io.socket.client.Socket;
import lol.ethan.may.Entry;
import lol.ethan.may.commands.Tracker;
import lol.ethan.may.utils.MayStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

public class LunaWebsocket {
    private static final String API_KEY = "LunaAPI-f6d3e83fe7bb42d2b2f4b8ba369bab85";
    public static Socket socket = null;

    public LunaWebsocket() {
        connect();
    }
    public static void connect() {
        try {
            IO.Options options = new IO.Options();
            options.auth = new HashMap<>();
            options.auth.put("key", API_KEY);

            socket = IO.socket("wss://luna-hub.tailad4b7d.ts.net", options);
            socket.on(Socket.EVENT_CONNECT, args -> {
                Entry.chat("connected to luna ws");
            });
            socket.on(Socket.EVENT_DISCONNECT, args -> {
                Entry.chat("disconnected from luna ws");
            });
            socket.on("playerJoined", args -> {
                try {
                    JSONObject response = (JSONObject) args[0];
                    String lobbyType = response.getString("mode");
                    int lobbyId = response.getInt("lobbyId");
                    String ign = response.getString("username");
                    if (Tracker.tracked.contains(ign.toLowerCase())) {
                        Entry.chat(Entry.PREFIX + "§3" + ign + " §ajoined §e" + MayStringUtils.capitalizeText(lobbyType) + " Lobby " + lobbyId);
                    }
                } catch (Exception e) {
                    System.out.println(Arrays.toString(args));
                    e.printStackTrace();
                }
            });
            socket.on("playerLeft", args -> {
                try {
                    JSONObject response = (JSONObject) args[0];
                    int lobbyId = response.getInt("lobbyId");
                    String ign = response.getString("username");
                    if (!Tracker.tracked.contains(ign.toLowerCase())) return;
                    String lobbyType = response.getString("mode");
                    switch (lobbyType) {
                        case "doubles":
                        case "threes":
                        case "fours":
                        case "4v4":
                        case "duels":
                        case "solos":
                        case "dreams":
                            Entry.chat(Entry.PREFIX + "§3" + ign + " §aqueued §eBedwars " + MayStringUtils.capitalizeText(lobbyType));
                            return;
                    }

                    Entry.chat(Entry.PREFIX + "§3" + ign + " §cleft §e" + MayStringUtils.capitalizeText(lobbyType) + " Lobby " + lobbyId);
                } catch (Exception e) {
                    System.out.println(Arrays.toString(args));
                    e.printStackTrace();
                }
            });
            socket.connect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

 */