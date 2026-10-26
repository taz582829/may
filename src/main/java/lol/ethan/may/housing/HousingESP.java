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
    public void onRenderWorldLast(RenderWorldEvent event) {
        if (!Config.housingEsp.enabled || this.mc.thePlayer == null || !this.mc.thePlayer.capabilities.isCreativeMode) {
            return;
        }
        if (HousingUtils.isInHousing()) {
            for (Entity entity : this.mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;
                    if (player.equals(this.mc.thePlayer) || player.getUniqueID().version() == 2 || Config.housingEspInvisOnly.enabled && !AntiInvis.INVISIBLE.contains(player))
                        continue;
                    String hex = "FFFFFF";
                    if (player.getTeam() != null) {
                        ScorePlayerTeam team = (ScorePlayerTeam) player.getTeam();
                        hex = ColorUtils.colorToHex(String.valueOf(ColorUtils.getLastColorCode(team.getColorSuffix())));
                    }
                    int c = Integer.parseInt(hex, 16);
                    Color _c = new Color(c);
                    Color color = new Color(_c.getRed(), _c.getGreen(), _c.getBlue(), 100);
                    RenderUtils.drawEntityBoundingBox(player, color);
                    continue;
                }
                if (!(entity instanceof EntityItemFrame)) continue;
                EntityItemFrame frame = (EntityItemFrame) entity;
                if (frame.getDisplayedItem() == null) {
                    RenderUtils.drawEntityBoundingBox(frame, this.EMPTY_FRAME_COLOR);
                    continue;
                }
                RenderUtils.drawEntityBoundingBox(frame, this.FULL_FRAME_COLOR);
            }
        }
    }
}

