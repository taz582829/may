package lol.ethan.may.events;

import net.minecraft.network.Packet;

public class PacketEvent {

    public static class Receive extends Event {
        private Packet<?> packet;

        public Receive(final Packet<?> packet) {
            this.packet = packet;
        }

        public Packet<?> getPacket() {
            return packet;
        }

        public void setPacket(final Packet<?> packet) {
            this.packet = packet;
        }
    }

    public static class Send extends Event {
        private Packet<?> packet;

        public Send(final Packet<?> packet) {
            this.packet = packet;
        }

        public Packet<?> getPacket() {
            return packet;
        }

        public void setPacket(final Packet<?> packet) {
            this.packet = packet;
        }
    }
}

