package lol.ethan.may.housing;

import lol.ethan.may.config.Config;
import lol.ethan.may.events.Priority;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.player.NameFormatEvent;
import lol.ethan.may.events.player.RenderLivingEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class AntiInvis {
    public static final Set<EntityPlayer> INVISIBLE = Collections.newSetFromMap(new WeakHashMap());
    Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderPre(final RenderLivingEvent.Pre event) {
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }
        final EntityPlayer player = (EntityPlayer) event.getEntity();
        if (INVISIBLE.contains(player) && HousingUtils.isInHousing() && Config.antiInvis.enabled) {
            event.getRenderer().renderName(player, event.getX(), event.getY(), event.getZ());
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

