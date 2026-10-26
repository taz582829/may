package lol.ethan.may.anticheat;

import lol.ethan.may.Entry;
import lol.ethan.may.anticheat.data.AnticheatData;
import lol.ethan.may.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumChatFormatting;

public abstract class AnticheatFlag {
    public final EnumChatFormatting color;
    protected final Minecraft mc = Minecraft.getMinecraft();
    public String name;
    public int threshold;

    public AnticheatFlag(String name, int threshold, EnumChatFormatting color) {
        this.name = name;
        this.threshold = threshold;
        this.color = color;
    }

    public AnticheatFlag(int threshold, EnumChatFormatting color) {
        this.name = this.getClass().getSimpleName();
        this.threshold = threshold;
        this.color = color;
    }

    protected int getFlags(EntityPlayer player) {
        AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());
        return data.flags.getOrDefault(this, 0);
    }

    protected void addFlag(EntityPlayer player, int count) {
        AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());
        int total = data.flags.getOrDefault(this, 0) + count;
        data.flags.put(this, total);
        if (total >= this.threshold) {
            this.sendFlagMessage(player);
            data.violations.put(this, data.violations.getOrDefault(this, 0) + 1);
        }
    }

    protected void addFlag(EntityPlayer player) {
        this.addFlag(player, 1);
    }

    protected void removeFlag(EntityPlayer player, int count) {
        AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());
        data.flags.put(this, Math.max(data.flags.getOrDefault(this, 0) - count, 0));
    }

    protected void removeFlag(EntityPlayer player) {
        this.removeFlag(player, 1);
    }

    protected void sendFlagMessage(EntityPlayer player) {
        AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());
        data.flags.remove(this);
        String display = ScorePlayerTeam.formatPlayerName(player.getTeam(), player.getName());
        Entry.chat("§dMay §8» §r§r" + display + " §rflags §5" + this.name);
        if (Config.flagPlaySound.enabled) {
            Entry.playSound("note.pling", 1.0f, 2.0f);
        }
    }
}

