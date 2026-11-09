package lol.ethan.may.commands;

import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;

public class SimulateCommand extends Command {
    public SimulateCommand() {
        super("simulate");
    }

    @Override
    public void execute(final String[] arr) {
        final String args = String.join(" ", arr).replaceAll("&(.)", "§$1");
        final S02PacketChat packet = new S02PacketChat(new ChatComponentText(args), (byte) 0);
        mc.getNetHandler().handleChat(packet);
    }
}
