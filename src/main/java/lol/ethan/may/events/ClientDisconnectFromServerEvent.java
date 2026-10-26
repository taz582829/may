package lol.ethan.may.events;

import net.minecraft.util.IChatComponent;

public class ClientDisconnectFromServerEvent extends Event {
    private IChatComponent reason;

    public ClientDisconnectFromServerEvent(IChatComponent reason) {
        this.reason = reason;
    }

    public IChatComponent getReason() {
        return this.reason;
    }

    public void setReason(IChatComponent reason) {
        this.reason = reason;
    }
}

