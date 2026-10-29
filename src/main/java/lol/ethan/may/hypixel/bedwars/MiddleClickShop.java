package lol.ethan.may.hypixel.bedwars;

import lol.ethan.may.config.Config;
import lol.ethan.may.events.SlotClickEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.utils.GuiUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StringUtils;

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
    public void onSlotClick(final SlotClickEvent event) {
        if (GuiScreen.isShiftKeyDown()) return;
        if (!Config.middleClickShop.enabled) return;
        final String guiTitle = GuiUtils.getTitle();
        if (guiTitle == null || guiTitle.trim().isEmpty()) return;

        final String clean = StringUtils.stripControlCodes(guiTitle.trim());

        if (this.shopNames.contains(clean)) {
            event.setClickType(3);
        }
    }
}
