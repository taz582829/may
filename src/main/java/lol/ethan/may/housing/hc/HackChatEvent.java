package lol.ethan.may.housing.hc;

import lol.ethan.may.events.Event;

import java.util.List;

public class HackChatEvent {

    public static class OnlineSet extends Event {
        public List<String> nicks;

        public OnlineSet(final List<String> nicks) {
            this.nicks = nicks;
        }

        public String buildMessage() {
            return "§8* users online: " + String.join(", ", nicks);
        }
    }

    public static class Info extends Event {
        public String text;

        public Info(final String text) {
            this.text = text;
        }

        public String buildMessage() {
            return "§a- " + text;
        }
    }

    public static class OnlineRemove extends Event {
        public String nick;

        public OnlineRemove(final String nick) {
            this.nick = nick;
        }

        public String buildMessage() {
            return "§8*" + nick + " left.";
        }
    }

    public static class OnlineAdd extends Event {
        public String nick;

        public OnlineAdd(final String nick) {
            this.nick = nick;
        }

        public String buildMessage() {
            return "§8*" + nick + " joined.";
        }
    }

    public static class Chat extends Event {
        public String nick;
        public String text;
        public String color;

        public Chat(final String nick, final String text, final String color) {
            this.nick = nick;
            this.text = text;
            this.color = color;
        }

        public String buildMessage() {
            return "§d" + nick + "§a > " + text;
        }
    }
}

