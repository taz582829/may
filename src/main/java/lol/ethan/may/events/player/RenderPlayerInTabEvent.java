package lol.ethan.may.events.player;

import lol.ethan.may.events.Event;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class RenderPlayerInTabEvent extends Event {
    public final NetworkPlayerInfo playerInfo;
    public String text;

    public RenderPlayerInTabEvent(final IChatComponent text, final NetworkPlayerInfo playerInfo) {
        this.text = text == null ? ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()) : text.getFormattedText();
        this.playerInfo = playerInfo;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public NetworkPlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public IChatComponent getTextAsComponent() {
        return new ChatComponentText(text);
    }
}

