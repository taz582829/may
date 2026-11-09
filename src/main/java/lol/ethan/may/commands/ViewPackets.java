package lol.ethan.may.commands;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledHeapByteBuf;
import lol.ethan.may.Entry;
import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.lang.reflect.Field;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewPackets extends Command {
    public enum ViewPacketsState {
        INCOMING("§aINCOMING"),
        OUTGOING("§eOUTGOING"),
        BOTH("§bBOTH"),
        DISABLED("§cDISABLED");

        public final String displayName;

        ViewPacketsState(final String displayName) {
            this.displayName = displayName;
        }
    }
    public ViewPacketsState state = ViewPacketsState.DISABLED;
    public List<String> filter = new ArrayList<>();

    public ViewPackets() {
        super("viewpackets");
    }

    @Override
    public void execute(final String[] args) {
        filter.clear();
        if (args.length == 0 || state != ViewPacketsState.DISABLED) {
            state = ViewPacketsState.DISABLED;

            Entry.chat(Entry.PREFIX + "Set packet viewing state to §cDISABLED§7!");
            EventBus.unsubscribe(this);
            return;
        }
        switch (args[0]) {
            case "inc":
            case "incoming":
                state = ViewPacketsState.INCOMING;
                break;
            case "out":
            case "outgoing":
                state = ViewPacketsState.OUTGOING;
                break;
            case "both":
                state = ViewPacketsState.BOTH;
                break;
            default:
                Entry.chat(Entry.PREFIX + "Set packet viewing state to §cDISABLED§7!");
                return;
        }
        if (args.length > 1) {
            filter.addAll(Arrays.asList(Arrays.copyOfRange(args, 1, args.length)));
        }

        Entry.chat(Entry.PREFIX + "Set packet viewing state to " + state.displayName);
        EventBus.subscribe(this);
    }
    @SubscribeEvent
    public void onPacketSend(final PacketEvent event) {
        if (event instanceof PacketEvent.Send && (state == ViewPacketsState.BOTH || state == ViewPacketsState.OUTGOING)) {
            final PacketEvent.Send send = (PacketEvent.Send) event;
            final IChatComponent component = buildMessage(send, false);
            final String packetName = send.getPacket().getClass().getSimpleName();
            boolean isAllowed = filter.size() == 0;
            if (!isAllowed) {
                for (final String w : filter) {
                    if (packetName.toLowerCase().contains(w.toLowerCase())) {
                        isAllowed = true;
                        break;
                    }
                }
            }
            if (!isAllowed) return;
            Entry.chat(component);
        } else if (event instanceof PacketEvent.Receive && (state == ViewPacketsState.BOTH || state == ViewPacketsState.INCOMING)) {
            final PacketEvent.Receive receive = (PacketEvent.Receive) event;
            final IChatComponent component = buildMessage(receive, true);
            final boolean isAllowed = getIsAllowed(receive);
            if (!isAllowed) return;
            Entry.chat(component);
        }
    }
    private boolean getIsAllowed(final PacketEvent.Receive receive) {
        final String packetName = receive.getPacket().getClass().getSimpleName();
        boolean isAllowed = filter.isEmpty();
        if (!isAllowed) {
            for (final String w : filter) {
                if (packetName.toLowerCase().contains(w.toLowerCase())) {
                    isAllowed = true;
                    break;
                }
            }
        }
        return isAllowed;
    }

    private IChatComponent buildMessage(final PacketEvent event, final boolean incoming) {
        final Packet<?> packet = event.getPacket();
        final ChatComponentText newComponent = new ChatComponentText(Entry.PREFIX + (incoming ? "§a" : "§e") + packet.getClass().getSimpleName());
        if (event.isCancelled()) {
            newComponent.appendText(" §4[CANCELLED]");
        }
        newComponent.appendText(" §9[COPY]");
        final ChatComponentText tooltip = new ChatComponentText("");
        Class<?> clazz = packet.getClass();
        final StringBuilder packetInfo = new StringBuilder();
        while (clazz != null) {
            for (final Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    tooltip.appendText("§7").appendText(field.getName()).appendText(": §r");
                    packetInfo.append(field.getName()).append(": ");
                    final Object result = field.get(packet);
                    if (result instanceof ItemStack) {
                        final ItemStack item = (ItemStack) result;
                        tooltip.appendSibling(item.getChatComponent());
                        final NBTTagCompound nbt = new NBTTagCompound();
                        item.writeToNBT(nbt);
                        packetInfo.append(nbt);
                    } else if (result instanceof IChatComponent) {
                        final IChatComponent component = (IChatComponent) result;
                        tooltip.appendSibling(component);
                        packetInfo.append(IChatComponent.Serializer.componentToJson(component));
                    } else if (result instanceof ByteBuf) {
                        final ByteBuf buf = (ByteBuf) result;
                        final int len = buf.writerIndex();
                        final byte[] bytes = new byte[len];
                        buf.getBytes(0, bytes);
                        final String str = new String(bytes, StandardCharsets.UTF_8);
                        packetInfo.append(str);
                        tooltip.appendText(str);
                    } else {
                        tooltip.appendText(result.toString());
                        packetInfo.append(result);
                    }
                    packetInfo.append("\n");
                    tooltip.appendText("\n");

                } catch (final Exception ignored) {
                }

            }
            clazz = clazz.getSuperclass();

        }
        final Runnable onClick = () -> {
            Entry.chat("§aCopied packet information to clipboard!");
            GuiScreen.setClipboardString(packetInfo.toString());
        };
        final ChatStyle style = new ChatStyle();
        style.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Entry.addInternalCommand(onClick)));
        style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip));
        newComponent.setChatStyle(style);
        return newComponent;
    }
    private void buildFields(final Object obj, final IChatComponent tooltip) {
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            for (final Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    tooltip.appendText("§7").appendText(field.getName()).appendText(": §r");
                    final Object result = field.get(obj);
                    if (result instanceof ItemStack) {
                        final ItemStack item = (ItemStack) result;
                        tooltip.appendSibling(item.getChatComponent());
                        final NBTTagCompound nbt = new NBTTagCompound();
                        item.writeToNBT(nbt);
                    } else if (result instanceof IChatComponent) {
                        final IChatComponent component = (IChatComponent) result;
                        tooltip.appendSibling(component);
                    } else if (result instanceof Class<?>) {

                    } else {
                        tooltip.appendText(result.toString());
                    }

                    tooltip.appendText("\n");

                } catch (final Exception ignored) {
                }

            }
            clazz = clazz.getSuperclass();
        }
    }

    @Override
    public String[] getTabCompletes(final String[] args) {
        return new String[]{ "out", "outgoing", "inc", "incoming" };
    }
}
