package lol.ethan.may.commands;

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

        ViewPacketsState(String displayName) {
            this.displayName = displayName;
        }
    }
    public ViewPacketsState state = ViewPacketsState.DISABLED;
    public List<String> filter = new ArrayList<>();

    public ViewPackets() {
        super("viewpackets");
    }

    @Override
    public void execute(String[] strings) {
        String[] args = Arrays.copyOfRange(strings, 1, strings.length);
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
    public void onPacketSend(PacketEvent event) {
        if (event instanceof PacketEvent.Send && (state == ViewPacketsState.BOTH || state == ViewPacketsState.OUTGOING)) {
            PacketEvent.Send send = (PacketEvent.Send) event;
            IChatComponent component = buildMessage(send, false);
            String packetName = send.getPacket().getClass().getSimpleName();
            boolean isAllowed = filter.size() == 0;
            if (!isAllowed) {
                for (String w : filter) {
                    if (packetName.toLowerCase().contains(w.toLowerCase())) {
                        isAllowed = true;
                        break;
                    }
                }
            }
            if (!isAllowed) return;
            Entry.chat(component);
        } else if (event instanceof PacketEvent.Receive && (state == ViewPacketsState.BOTH || state == ViewPacketsState.INCOMING)) {
            PacketEvent.Receive receive = (PacketEvent.Receive) event;
            IChatComponent component = buildMessage(receive, true);
            boolean isAllowed = getIsAllowed(receive);
            if (!isAllowed) return;
            Entry.chat(component);
        }
    }
    private boolean getIsAllowed(PacketEvent.Receive receive) {
        String packetName = receive.getPacket().getClass().getSimpleName();
        boolean isAllowed = filter.isEmpty();
        if (!isAllowed) {
            for (String w : filter) {
                if (packetName.toLowerCase().contains(w.toLowerCase())) {
                    isAllowed = true;
                    break;
                }
            }
        }
        return isAllowed;
    }

    private IChatComponent buildMessage(PacketEvent event, boolean incoming) {
        Packet<?> packet = event.getPacket();
        ChatComponentText newComponent = new ChatComponentText(Entry.PREFIX + (incoming ? "§a" : "§e") + packet.getClass().getSimpleName());
        if (event.isCancelled()) {
            newComponent.appendText(" §4[CANCELLED]");
        }
        newComponent.appendText(" §9[COPY]");
        ChatComponentText tooltip = new ChatComponentText("");
        Class<?> clazz = packet.getClass();
        StringBuilder packetInfo = new StringBuilder();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    tooltip.appendText("§7").appendText(field.getName()).appendText(": §r");
                    packetInfo.append(field.getName()).append(": ");
                    Object result = field.get(packet);
                    if (result instanceof ItemStack) {
                        ItemStack item = (ItemStack) result;
                        tooltip.appendSibling(item.getChatComponent());
                        NBTTagCompound nbt = new NBTTagCompound();
                        item.writeToNBT(nbt);
                        packetInfo.append(nbt);
                    } else if (result instanceof IChatComponent) {
                        IChatComponent component = (IChatComponent) result;
                        tooltip.appendSibling(component);
                        packetInfo.append(IChatComponent.Serializer.componentToJson(component));
                    } else {
                        tooltip.appendText(ReflectionToStringBuilder.toString(result));
                        packetInfo.append(result);
                    }
                    packetInfo.append("\n");
                    tooltip.appendText("\n");

                } catch (Exception ignored) {
                }

            }
            clazz = clazz.getSuperclass();

        }
        Runnable onClick = () -> {
            Entry.chat("§aCopied packet information to clipboard!");
            GuiScreen.setClipboardString(packetInfo.toString());
        };
        ChatStyle style = new ChatStyle();
        style.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Entry.addInternalCommand(onClick)));
        style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip));
        newComponent.setChatStyle(style);
        return newComponent;
    }

    private void buildFields(Object obj, IChatComponent tooltip) {
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    tooltip.appendText("§7").appendText(field.getName()).appendText(": §r");
                    Object result = field.get(obj);
                    if (result instanceof ItemStack) {
                        ItemStack item = (ItemStack) result;
                        tooltip.appendSibling(item.getChatComponent());
                        NBTTagCompound nbt = new NBTTagCompound();
                        item.writeToNBT(nbt);
                    } else if (result instanceof IChatComponent) {
                        IChatComponent component = (IChatComponent) result;
                        tooltip.appendSibling(component);
                    } else if (result instanceof Class<?>) {

                    } else {
                        tooltip.appendText(result.toString());
                    }

                    tooltip.appendText("\n");

                } catch (Exception ignored) {
                }

            }
            clazz = clazz.getSuperclass();
        }
    }

    @Override
    public String[] getTabCompletes(String[] args) {
        return new String[]{ "out", "outgoing", "inc", "incoming" };
    }
}
