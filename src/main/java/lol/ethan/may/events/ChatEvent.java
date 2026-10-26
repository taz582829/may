package lol.ethan.may.events;

import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;

public class ChatEvent {

    public static class Received
            extends Event {
        private IChatComponent message;
        private byte type;

        public Received(IChatComponent message, byte type) {
            this.message = message;
            this.type = type;
        }

        public IChatComponent getMessage() {
            return this.message;
        }

        public void setMessage(IChatComponent message) {
            this.message = message;
        }

        public byte getType() {
            return this.type;
        }

        public void setType(byte type) {
            this.type = type;
        }

        public String getUnformattedMessage() {
            return StringUtils.stripControlCodes(this.message.getUnformattedText());
        }
    }

    public static class Send
            extends Event {
        private String message;

        public Send(String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

