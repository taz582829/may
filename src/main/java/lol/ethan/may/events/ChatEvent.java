package lol.ethan.may.events;

import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;

public class ChatEvent {

    public static class Received extends Event {
        private IChatComponent message;

        public Received(final IChatComponent message) {
            this.message = message;
        }

        public IChatComponent getMessage() {
            return message;
        }

        public void setMessage(final IChatComponent message) {
            this.message = message;
        }

        public String getUnformattedMessage() {
            return StringUtils.stripControlCodes(message.getUnformattedText());
        }
    }

    public static class Send extends Event {
        private String message;

        public Send(final String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(final String message) {
            this.message = message;
        }
    }
}

