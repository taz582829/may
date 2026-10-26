package lol.ethan.may.events.player;

import lol.ethan.may.events.Event;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class NameFormatInTabEvent extends Event {
    private final NetworkPlayerInfo playerInfo;
    private String text;

    public NameFormatInTabEvent(IChatComponent text, NetworkPlayerInfo playerInfo) {
        this.text = text == null ? ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()) : text.getFormattedText();
        this.playerInfo = playerInfo;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public NetworkPlayerInfo getPlayerInfo() {
        return this.playerInfo;
    }

    public IChatComponent getTextAsComponent() {
        return new ChatComponentText(this.text);
    }
}

