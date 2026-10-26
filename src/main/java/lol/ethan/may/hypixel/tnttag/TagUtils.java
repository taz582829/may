package lol.ethan.may.hypixel.tnttag;

import lol.ethan.may.commands.HypixelLocation;
import lol.ethan.may.hypixel.HypixelUtils;
import lol.ethan.may.hypixel.modapi.LocationData;
import net.hypixel.data.type.GameType;

public class TagUtils {
    public static boolean isInTag() {
        return HypixelUtils.isOnHypixel() &&
                LocationData.getGameType() == GameType.TNTGAMES &&
                LocationData.getMode() != null &&
                LocationData.getMode().equalsIgnoreCase("tntag");
    }
}
