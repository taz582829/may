package lol.ethan.may.commands;

import lol.ethan.may.Entry;
import lol.ethan.may.events.ChatEvent;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.mappings.Mappings;
import net.minecraft.client.Minecraft;
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
            matches = S3APacketTabComplete.class.getDeclaredField(
                    Mappings.getMappingType() == Mappings.MappingType.MCP ? Mappings.getField(S3APacketTabComplete.class, "matches") : "matches"
            );
        } catch (final Exception e) {
            try {
                matches = S3APacketTabComplete.class.getDeclaredField(
                        Mappings.getMappingType() == Mappings.MappingType.MCP ? Mappings.getField(S3APacketTabComplete.class, "matches") : "matches"
                );
            } catch (final NoSuchFieldException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    public static void register(final Command cmd) {
        commands.add(cmd);
    }

    public static void unregister(final Command cmd) {
        commands.remove(cmd);
    }

    @SubscribeEvent
    public void onPacket(final PacketEvent.Send event) {
        if (event.getPacket() instanceof C14PacketTabComplete) {
            final C14PacketTabComplete packet = (C14PacketTabComplete) event.getPacket();
            final String msg = packet.getMessage().trim();
            if (!msg.startsWith("/")) {
                return;
            }
            final String[] args = msg.split(" ");
            final String cmdName = args[0].substring(1);
            if (args.length == 1) {
                lastSuggestions = commands.stream().filter(c -> c.matches(cmdName)).map(Command::toString).toArray(String[]::new);
            } else {
                for (final Command cmd : commands) {
                    if (!cmd.name.startsWith(cmdName)) continue;
                    lastSuggestions = cmd.getTabCompletes(Arrays.stream(args).skip(1L).toArray(String[]::new));
                }
            }
        }
    }
    @SubscribeEvent
    public void onChatSend(ChatEvent.Send event) {
        final String msg = event.getMessage();
        if (!msg.startsWith("/")) return;
        final String[] parts = msg.substring(1).trim().split(" ");
        for (final Command cmd : commands) {
            if (cmd.name.equals(parts[0].toLowerCase())) {
                try {
                    cmd.execute(Arrays.stream(msg.split(" ")).skip(1L).toArray(String[]::new));
                } catch (final Exception e) {
                    e.printStackTrace();
                    Entry.chat("§cAn error occured while trying to run this command (check console)");
                }
                event.setCancelled(true);
                return;
            }
            for (final String alias : cmd.aliases) {
                if (!alias.equals(parts[0].toLowerCase())) continue;
                try {
                    cmd.execute(Arrays.stream(msg.split(" ")).skip(1L).toArray(String[]::new));
                } catch (final Exception e) {
                    e.printStackTrace();
                    Entry.chat("§cAn error occured while trying to run this command (check console)");
                }
                event.setCancelled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof S3APacketTabComplete && this.lastSuggestions != null) {
            final S3APacketTabComplete packet = (S3APacketTabComplete) event.getPacket();
            final String[] lastSuggestions = this.lastSuggestions;
            final String[] serverSuggestions = packet.func_149630_c();
            this.lastSuggestions = null;
            final ArrayList<String> list = new ArrayList<>();
            list.addAll(Arrays.asList(lastSuggestions));
            list.addAll(Arrays.asList(serverSuggestions));
            final String[] ordered = list.stream().distinct().sorted().toArray(String[]::new);
            try {
                matches.set(packet, ordered);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

