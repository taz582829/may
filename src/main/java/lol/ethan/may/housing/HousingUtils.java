package lol.ethan.may.housing;

import lol.ethan.may.hypixel.HypixelUtils;
import lol.ethan.may.hypixel.modapi.LocationData;
import net.hypixel.data.type.GameType;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.world.WorldSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class HousingUtils {
    private static final Pattern titlePattern = Pattern.compile("You are in (?<title>.*), by (?<ownerRank>.*)?\\s?(?<owner>\\w{2,16})");
    private static final Map<UUID, ItemStack> itemMap = new HashMap<>();
    static Minecraft mc = Minecraft.getMinecraft();

    public static UUID addToItemMap(final ItemStack stack) {
        final UUID id = UUID.randomUUID();
        itemMap.put(id, stack);
        return id;
    }

    public static boolean isInHousing() {
        return HypixelUtils.isOnHypixel() && GameType.HOUSING == LocationData.getGameType() && null == LocationData.getLobbyName();
    }

    public static boolean isInCreative() {
        if (null == HousingUtils.mc.thePlayer) {
            return false;
        }
        if (null == mc.getNetHandler() || null == mc.getNetHandler().getPlayerInfo(HousingUtils.mc.thePlayer.getUniqueID())) {
            return false;
        }
        return WorldSettings.GameType.CREATIVE == mc.getNetHandler().getPlayerInfo(HousingUtils.mc.thePlayer.getUniqueID()).getGameType();
    }

    public static void spawnItem(final ItemStack item) {
        if (!HousingUtils.mc.thePlayer.capabilities.isCreativeMode) {
            return;
        }
        final int handSlot = HousingUtils.mc.thePlayer.inventory.currentItem;
        HousingUtils.mc.thePlayer.sendQueue.addToSendQueue(new C10PacketCreativeInventoryAction(handSlot + 36, item));
    }

    public static void spawnItem(final UUID id) {
        if (!HousingUtils.mc.thePlayer.capabilities.isCreativeMode) {
            return;
        }
        final int handSlot = HousingUtils.mc.thePlayer.inventory.currentItem;
        final ItemStack item = itemMap.getOrDefault(id, null);
        HousingUtils.mc.thePlayer.sendQueue.addToSendQueue(new C10PacketCreativeInventoryAction(handSlot + 36, item));
    }

    public static void openItemStorage() {
        mc.addScheduledTask(() -> mc.displayGuiScreen(new ItemStorage()));
    }

    public static class HouseInfo {
        public final String owner;
        public final String ownerRank;
        public final String title;

        public HouseInfo(final String owner, final String ownerRank, final String title) {
            this.owner = owner;
            this.ownerRank = ownerRank;
            this.title = title;
        }
    }
}

