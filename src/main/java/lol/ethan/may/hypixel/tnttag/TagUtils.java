package lol.ethan.may.hypixel.tnttag;

import lol.ethan.may.hypixel.HypixelUtils;
import lol.ethan.may.hypixel.modapi.LocationData;
import net.hypixel.data.type.GameType;

public class TagUtils {
    public static boolean isInTag() {
        return HypixelUtils.isOnHypixel() &&
                GameType.TNTGAMES == LocationData.getGameType() &&
                null != LocationData.getMode() &&
                LocationData.getMode().equalsIgnoreCase("tntag");
    }
}
