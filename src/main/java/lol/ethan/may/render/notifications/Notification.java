package lol.ethan.may.render.notifications;

import net.minecraft.client.Minecraft;

public abstract class Notification {
    long lastTime = 0L;
    Minecraft mc = Minecraft.getMinecraft();

    public void drawNotification(String title, String desc, int time) {
    }
}

