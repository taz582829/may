package lol.ethan.may.housing;

import lol.ethan.may.config.Config;
import lol.ethan.may.events.RenderWorldEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.utils.ColorUtils;
import lol.ethan.may.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.awt.*;

public class HousingESP {
    final Color FULL_FRAME_COLOR = new Color(255, 0, 35, 99);
    final Color EMPTY_FRAME_COLOR = new Color(10, 255, 100, 100);
    Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderWorldLast(final RenderWorldEvent event) {
        if (!Config.housingEsp.enabled || null == this.mc.thePlayer || !mc.thePlayer.capabilities.isCreativeMode) {
            return;
        }
        if (HousingUtils.isInHousing()) {
            for (final Entity entity : mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityPlayer) {
                    final EntityPlayer player = (EntityPlayer) entity;
                    if (player.equals(mc.thePlayer) || 2 == player.getUniqueID().version() || Config.housingEspInvisOnly.enabled && !AntiInvis.INVISIBLE.contains(player))
                        continue;
                    String hex = "FFFFFF";
                    if (null != player.getTeam()) {
                        final ScorePlayerTeam team = (ScorePlayerTeam) player.getTeam();
                        hex = ColorUtils.colorToHex(String.valueOf(ColorUtils.getLastColorCode(team.getColorSuffix())));
                    }
                    final int c = Integer.parseInt(hex, 16);
                    final Color _c = new Color(c);
                    final Color color = new Color(_c.getRed(), _c.getGreen(), _c.getBlue(), 100);
                    RenderUtils.drawEntityBoundingBox(player, color);
                    continue;
                }
                if (!(entity instanceof EntityItemFrame)) continue;
                final EntityItemFrame frame = (EntityItemFrame) entity;
                if (null == frame.getDisplayedItem()) {
                    RenderUtils.drawEntityBoundingBox(frame, EMPTY_FRAME_COLOR);
                    continue;
                }
                RenderUtils.drawEntityBoundingBox(frame, FULL_FRAME_COLOR);
            }
        }
    }
}

