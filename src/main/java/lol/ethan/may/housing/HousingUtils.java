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

    public static UUID addToItemMap(ItemStack stack) {
        UUID id = UUID.randomUUID();
        itemMap.put(id, stack);
        return id;
    }

    public static boolean isInHousing() {
        return HypixelUtils.isOnHypixel() && LocationData.getGameType() == GameType.HOUSING && LocationData.getLobbyName() == null;
    }

    public static boolean isInCreative() {
        if (HousingUtils.mc.thePlayer == null) {
            return false;
        }
        if (mc.getNetHandler() == null || mc.getNetHandler().getPlayerInfo(HousingUtils.mc.thePlayer.getUniqueID()) == null) {
            return false;
        }
        return mc.getNetHandler().getPlayerInfo(HousingUtils.mc.thePlayer.getUniqueID()).getGameType() == WorldSettings.GameType.CREATIVE;
    }

    public static void spawnItem(ItemStack item) {
        if (!HousingUtils.mc.thePlayer.capabilities.isCreativeMode) {
            return;
        }
        int handSlot = HousingUtils.mc.thePlayer.inventory.currentItem;
        HousingUtils.mc.thePlayer.sendQueue.addToSendQueue(new C10PacketCreativeInventoryAction(handSlot + 36, item));
    }

    public static void spawnItem(UUID id) {
        if (!HousingUtils.mc.thePlayer.capabilities.isCreativeMode) {
            return;
        }
        int handSlot = HousingUtils.mc.thePlayer.inventory.currentItem;
        ItemStack item = itemMap.getOrDefault(id, null);
        HousingUtils.mc.thePlayer.sendQueue.addToSendQueue(new C10PacketCreativeInventoryAction(handSlot + 36, item));
    }

    public static void openItemStorage() {
        mc.addScheduledTask(() -> mc.displayGuiScreen(new ItemStorage()));
    }

    public static class HouseInfo {
        public final String owner;
        public final String ownerRank;
        public final String title;

        public HouseInfo(String owner, String ownerRank, String title) {
            this.owner = owner;
            this.ownerRank = ownerRank;
            this.title = title;
        }
    }
}

