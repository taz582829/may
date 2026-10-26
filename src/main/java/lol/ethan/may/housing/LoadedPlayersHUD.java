package lol.ethan.may.housing;

import lol.ethan.may.render.huds.BasicHud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LoadedPlayersHUD extends BasicHud {
    public LoadedPlayersHUD() {
        super(true, 20.0f, 20.0f);
    }

    @Override
    public boolean shouldShow() {
        return super.shouldShow() && this.mc.theWorld != null && this.mc.thePlayer != null && HousingUtils.isInHousing() && (HousingUtils.isInCreative() || this.mc.thePlayer.capabilities.isCreativeMode);
    }

    @Override
    protected List<String> getLines(boolean example) {
        ArrayList<String> lines = new ArrayList<>();
        List<EntityPlayer> players = this.mc.theWorld.playerEntities.stream().filter(p -> !p.equals(this.mc.thePlayer) && p.getUniqueID().version() != 2).collect(Collectors.toList());
        if (players.size() == 0) {
            return Collections.emptyList();
        }
        lines.add("§fLoaded Players §7(" + players.size() + ")§f:");
        for (EntityPlayer player : players) {
            double dist;
            StringBuilder text = new StringBuilder("§8- §r" + player.getDisplayName().getFormattedText());
            if (player.isInvisibleToPlayer(this.mc.thePlayer)) {
                text.append(" §b[I]");
            }
            EnumChatFormatting distanceColor = (dist = this.mc.thePlayer.getDistanceToEntity(player)) <= 10.0 ? EnumChatFormatting.DARK_GREEN : (dist <= 20.0 ? EnumChatFormatting.GREEN : (dist <= 30.0 ? EnumChatFormatting.GOLD : (dist <= 45.0 ? EnumChatFormatting.RED : EnumChatFormatting.DARK_RED)));
            text.append(" ").append(distanceColor).append(String.format("%.2f", dist)).append("m");
            lines.add(text.toString());
        }
        return lines;
    }
}

