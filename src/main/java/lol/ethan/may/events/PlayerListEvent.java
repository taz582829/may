package lol.ethan.may.events;

import net.minecraft.network.play.server.S38PacketPlayerListItem;

public class PlayerListEvent {

    public static class Remove
            extends Event {
        private final S38PacketPlayerListItem.AddPlayerData data;

        public Remove(S38PacketPlayerListItem.AddPlayerData data) {
            this.data = data;
        }

        public S38PacketPlayerListItem.AddPlayerData getPlayerData() {
            return this.data;
        }
    }

    public static class Add
            extends Event {
        private final S38PacketPlayerListItem.AddPlayerData data;

        public Add(S38PacketPlayerListItem.AddPlayerData data) {
            this.data = data;
        }

        public S38PacketPlayerListItem.AddPlayerData getPlayerData() {
            return this.data;
        }
    }
}

