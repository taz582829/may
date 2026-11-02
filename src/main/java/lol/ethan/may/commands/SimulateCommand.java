package lol.ethan.may.commands;

import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;

public class SimulateCommand extends Command {
    public SimulateCommand() {
        super("simulate");
    }

    @Override
    public void execute(String[] arr) {
        String args = String.join(" ", arr).replaceAll("&(.)", "§$1");
        S02PacketChat packet = new S02PacketChat(new ChatComponentText(args), (byte) 0);
        mc.getNetHandler().handleChat(packet);
    }
}
