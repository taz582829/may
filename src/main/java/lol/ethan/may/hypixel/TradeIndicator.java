package lol.ethan.may.hypixel;

import lol.ethan.may.config.Category;
import lol.ethan.may.config.component.Button;
import lol.ethan.may.config.component.ColorComponent;
import lol.ethan.may.render.huds.BasicHud;
import lol.ethan.may.render.huds.HUDHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class TradeIndicator extends BasicHud {

    public TradeIndicator() {
        super(true, 50, 50);
    }
    public Button centered = new Button("Centered HUD", Category.General, true, "centers the x coordinate of the hud");
    public Button showHeart = new Button("Show Heart Sign", Category.General, true, "shows a ❤ symbol next to the text");
    public ColorComponent winningTextColor = new ColorComponent("Winning Text Color", Category.General, 0x52D881);
    public ColorComponent losingTextColor = new ColorComponent("Losing Text Color", Category.General, 0xE14040);
    private EntityPlayer lastAttacked;
    private long lastAttackTime;
    private boolean winning;

    @Override
    public float[] overrideCoords(float x, float y) {
        if (centered.enabled && shouldShow()) {
            if (lineCache.size() == 0) return new float[]{ x, y };
            String line = lineCache.get(0);
            return new float[]{
                    HUDHelper.getCenteredX(line) / scale,
                    y / scale
            };
        }
        return new float[]{ x, y };
    }

    @Override
    public int getTextColor() {
        return winning ? winningTextColor.color : losingTextColor.color;
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = (C02PacketUseEntity) event.getPacket();
            if (packet.getAction() != C02PacketUseEntity.Action.ATTACK) return;
            if (!(packet.getEntityFromWorld(mc.theWorld) instanceof EntityPlayer)) return;
            lastAttacked = (EntityPlayer) packet.getEntityFromWorld(mc.theWorld);
            lastAttackTime = System.currentTimeMillis();
        }

    }
    @Override
    public boolean shouldShow() {
        return super.shouldShow() && mc.thePlayer != null && mc.theWorld != null && lastAttacked != null && System.currentTimeMillis() - lastAttackTime < 2000;
    }

    @Override
    protected List<String> getLines(boolean example) {
        if (example) {
            winning = true;
            return Collections.singletonList("+11.4" + (showHeart.enabled ? "❤" : ""));
        }
        float healthDifference = (lastAttacked.getHealth() + lastAttacked.getAbsorptionAmount()) / 2 - (mc.thePlayer.getHealth() + mc.thePlayer.getAbsorptionAmount()) / 2;
        if (healthDifference == 0) return Collections.emptyList();
        winning = (mc.thePlayer.getHealth() + mc.thePlayer.getAbsorptionAmount()) > (lastAttacked.getHealth() + lastAttacked.getAbsorptionAmount());
        return Collections.singletonList((winning ? "+" : "-") + String.format("%.2f", Math.abs(healthDifference)) + "❤");
    }
}
