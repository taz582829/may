package lol.ethan.may.hypixel;

import lol.ethan.may.config.Category;
import lol.ethan.may.config.component.Button;
import lol.ethan.may.config.component.ColorComponent;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.render.huds.BasicHud;
import lol.ethan.may.render.huds.HUDHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class TradeIndicator extends BasicHud {
    public Button centered = new Button("Centered HUD", Category.General, true, "centers the x coordinate of the hud");
    public Button showHeart = new Button("Show Heart Sign", Category.General, true, "shows a ❤ symbol next to the text");
    public ColorComponent winningTextColor = new ColorComponent("Winning Text Color", Category.General, 5429377);
    public ColorComponent losingTextColor = new ColorComponent("Losing Text Color", Category.General, 14762048);
    private EntityPlayer lastAttacked;
    private long lastAttackTime;
    private boolean winning;

    public TradeIndicator() {
        super(true, 50.0f, 50.0f);
    }

    @Override
    public float[] overrideCoords(float x, float y) {
        if (this.centered.enabled && this.shouldShow()) {
            if (this.lineCache.size() == 0) {
                return new float[]{x, y};
            }
            String line = this.lineCache.get(0);
            return new float[]{HUDHelper.getCenteredX(line) / this.scale, y / this.scale};
        }
        return new float[]{x, y};
    }

    @Override
    public int getTextColor() {
        return this.winning ? this.winningTextColor.color : this.losingTextColor.color;
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = (C02PacketUseEntity) event.getPacket();
            if (packet.getAction() != C02PacketUseEntity.Action.ATTACK) {
                return;
            }
            if (!(packet.getEntityFromWorld(this.mc.theWorld) instanceof EntityPlayer)) {
                return;
            }
            this.lastAttacked = (EntityPlayer) packet.getEntityFromWorld(this.mc.theWorld);
            this.lastAttackTime = System.currentTimeMillis();
        }
    }

    @Override
    public boolean shouldShow() {
        return super.shouldShow() && this.mc.thePlayer != null && this.mc.theWorld != null && this.lastAttacked != null && System.currentTimeMillis() - this.lastAttackTime < 2000L;
    }

    @Override
    protected List<String> getLines(boolean example) {
        if (example) {
            this.winning = true;
            return Collections.singletonList("+11.4" + (this.showHeart.enabled ? "❤" : ""));
        }
        float healthDifference = (this.lastAttacked.getHealth() + this.lastAttacked.getAbsorptionAmount()) / 2.0f - (this.mc.thePlayer.getHealth() + this.mc.thePlayer.getAbsorptionAmount()) / 2.0f;
        if (healthDifference == 0.0f) {
            return Collections.emptyList();
        }
        this.winning = this.mc.thePlayer.getHealth() + this.mc.thePlayer.getAbsorptionAmount() > this.lastAttacked.getHealth() + this.lastAttacked.getAbsorptionAmount();
        return Collections.singletonList((this.winning ? "+" : "-") + String.format("%.2f", Float.valueOf(Math.abs(healthDifference))) + "❤");
    }
}

