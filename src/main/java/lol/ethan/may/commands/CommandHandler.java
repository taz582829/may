package lol.ethan.may.commands;

import lol.ethan.may.Entry;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.mappings.Mappings;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHandler {
    private static final List<Command> commands = new ArrayList<>();
    Minecraft mc = Minecraft.getMinecraft();
    private Field matches;
    private String[] lastSuggestions;

    public CommandHandler() {
        try {
            this.matches = S3APacketTabComplete.class.getDeclaredField("matches");
        } catch (Exception e) {
            try {
                this.matches = S3APacketTabComplete.class.getDeclaredField(Mappings.getField(S3APacketTabComplete.class, "matches"));
            } catch (NoSuchFieldException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    public static void register(Command cmd) {
        commands.add(cmd);
    }

    public static void unregister(Command cmd) {
        commands.remove(cmd);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof C14PacketTabComplete) {
            C14PacketTabComplete packet = (C14PacketTabComplete) event.getPacket();
            String msg = packet.getMessage().trim();
            if (!msg.startsWith("/")) {
                return;
            }
            String[] args = msg.split(" ");
            String cmdName = args[0].substring(1);
            if (args.length == 1) {
                this.lastSuggestions = commands.stream().filter(c -> c.matches(cmdName)).map(Command::toString).toArray(String[]::new);
            } else {
                for (Command cmd : commands) {
                    if (!cmd.name.startsWith(cmdName)) continue;
                    this.lastSuggestions = cmd.getTabCompletes(Arrays.stream(args).skip(1L).toArray(String[]::new));
                }
            }
        }
        if (event.getPacket() instanceof C01PacketChatMessage) {
            String msg = ((C01PacketChatMessage) event.getPacket()).getMessage();
            if (!msg.startsWith("/")) return;
            String[] parts = msg.substring(1).trim().split(" ");
            for (Command cmd : commands) {
                if (cmd.name.equals(parts[0].toLowerCase())) {
                    try {
                        cmd.execute(Arrays.stream(msg.split(" ")).skip(1L).toArray(String[]::new));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Entry.chat("§cAn error occured while trying to run this command (check console)");
                    }
                    event.setCancelled(true);
                    return;
                }
                for (String alias : cmd.aliases) {
                    if (!alias.equals(parts[0].toLowerCase())) continue;
                    try {
                        cmd.execute(Arrays.stream(msg.split(" ")).skip(1L).toArray(String[]::new));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Entry.chat("§cAn error occured while trying to run this command (check console)");
                    }
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof S3APacketTabComplete && this.lastSuggestions != null) {
            S3APacketTabComplete packet = (S3APacketTabComplete) event.getPacket();
            String[] lastSuggestions = this.lastSuggestions;
            String[] serverSuggestions = packet.func_149630_c();
            this.lastSuggestions = null;
            ArrayList<String> list = new ArrayList<>();
            list.addAll(Arrays.asList(lastSuggestions));
            list.addAll(Arrays.asList(serverSuggestions));
            String[] ordered = list.stream().distinct().sorted().toArray(String[]::new);
            try {
                this.matches.set(packet, ordered);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

