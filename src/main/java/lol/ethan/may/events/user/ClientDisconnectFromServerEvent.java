package lol.ethan.may.events.user;

import lol.ethan.may.events.Event;
import net.minecraft.util.IChatComponent;

public class ClientDisconnectFromServerEvent extends Event {
    private IChatComponent reason;

    public ClientDisconnectFromServerEvent(final IChatComponent reason) {
        this.reason = reason;
    }

    public IChatComponent getReason() {
        return reason;
    }

    public void setReason(final IChatComponent reason) {
        this.reason = reason;
    }
}

