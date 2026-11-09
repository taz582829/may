package lol.ethan.may.housing.hc;

import lol.ethan.may.Entry;
import lol.ethan.may.commands.Command;

import java.util.Arrays;
import java.util.Set;

public class HackChatCommand extends Command {

    public HackChatCommand() {
        super("hackchat", "hc");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length == 0) return;
        final String arg = args[0];
        switch (arg.toLowerCase()) {
            case "connect":
                final String id = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                HackChatHandler.joinChannel(id);
                Entry.chat("§aSuccessfully connected to §7" + id + "§a!\n§8To talk, say \">\" and then the message you want to say.");
                break;
            case "disconnect":
            case "dc":
                HackChatHandler.disconnect();
                Entry.chat("§aSuccessfully disconnected.");
                break;
            case "nick":
                final String nick = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                HackChatHandler.setNick(nick);
                Entry.chat("§aSuccessfully set nick to " + nick + "!");
                break;
            case "list":
                final Set<String> online = HackChatHandler.getOnlineSet();
                Entry.chat("§7users online " + online.size() + ": " + String.join(", ", online));
                break;
        }
    }

    @Override
    public String[] getTabCompletes(final String[] args) {
        return new String[]{ "connect", "disconnect", "dc", "nick", "list" };
    }
}