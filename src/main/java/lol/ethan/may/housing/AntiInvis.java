package lol.ethan.may.housing;

import com.sun.tools.javah.Gen;
import lol.ethan.may.config.Config;
import lol.ethan.may.events.Priority;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.TickEvent;
import lol.ethan.may.events.player.NameFormatEvent;
import lol.ethan.may.events.player.RenderLivingEvent;
import lol.ethan.may.events.render.RenderModelEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

public class AntiInvis {
    public static final Set<EntityPlayer> INVISIBLE = new HashSet<>();
    Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderPre(final RenderLivingEvent.Pre event) {
        if (!HousingUtils.isInHousing()) return;
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }
        final EntityPlayer player = (EntityPlayer) event.getEntity();
        if (!player.isInvisible()) return;
        player.setInvisible(false);
        AntiInvis.INVISIBLE.add(player);
    }
    @SubscribeEvent
    public void onRenderPost(final RenderLivingEvent.Post event) {
        if (!HousingUtils.isInHousing()) return;
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }
        final EntityPlayer player = (EntityPlayer) event.getEntity();
        if (AntiInvis.INVISIBLE.remove(player)) {
            player.setInvisible(true);
        }
    }

    @SubscribeEvent
    public void onRenderModelPre(final RenderModelEvent.Pre event) {
        if (!HousingUtils.isInHousing()) return;
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }
        final EntityPlayer player = (EntityPlayer) event.getEntity();
        if (!AntiInvis.INVISIBLE.contains(player)) return;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA
        );
        GlStateManager.color(1F, 1F, 1F, 0.4F);
    }
    @SubscribeEvent
    public void onRenderModelPost(final RenderModelEvent.Post event) {
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }
        final EntityPlayer player = (EntityPlayer) event.getEntity();
        if (!AntiInvis.INVISIBLE.contains(player)) return;
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableBlend();
    }

    @SubscribeEvent
    public void onTick(final TickEvent.Post event) {
        if (mc.thePlayer == null || mc.theWorld == null || !HousingUtils.isInHousing()) return;
        for (final EntityPlayer player : mc.theWorld.playerEntities) {
            if (player.isInvisible()) INVISIBLE.add(player);
            else INVISIBLE.remove(player);
        }
    }

    @SubscribeEvent(priority = Priority.LOW)
    public void onNameRender(final NameFormatEvent event) {
        final EntityPlayer player = event.getPlayer();
        if (INVISIBLE.contains(player) && HousingUtils.isInHousing() && Config.antiInvis.enabled) {
            event.setText(event.getText() + " §b[I]");
        }
    }
}

