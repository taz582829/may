package lol.ethan.may.hypixel.bedwars;

import lol.ethan.may.Entry;
import lol.ethan.may.events.SlotClickEvent;
import lol.ethan.may.utils.GuiUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StringUtils;
import lol.ethan.may.events.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

public class MiddleClickShop {
    List<String> shopNames = Arrays.asList(
            "Quick Buy",
            "Blocks",
            "Melee",
            "Armor",
            "Tools",
            "Ranged",
            "Potions",
            "Rotating Items",
            "Upgrades & Traps",
            "Utility",
            "Guns",
            "frost-test-gui"
    );
    @SubscribeEvent
    public void onSlotClick(SlotClickEvent event) {
        if (GuiScreen.isShiftKeyDown()) return;
        String guiTitle = GuiUtils.getTitle();
        if (guiTitle == null || guiTitle.trim().isEmpty()) return;

        String clean = StringUtils.stripControlCodes(guiTitle.trim());

        if (shopNames.contains(clean)) {
            event.setClickType(3);
        }
    }
}
