package lol.ethan.may.events.player;

import lol.ethan.may.events.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class NameFormatEvent extends Event {
    private final EntityPlayer player;
    private String text;

    public NameFormatEvent(final IChatComponent text, final EntityPlayer player) {
        this.text = text == null ? player.getName() : text.getFormattedText();
        this.player = player;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public IChatComponent getTextAsComponent() {
        return new ChatComponentText(text);
    }
}

