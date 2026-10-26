package lol.ethan.may.housing;

import lol.ethan.may.Entry;
import lol.ethan.may.events.MouseEvent;
import lol.ethan.may.events.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.util.MovingObjectPosition;

public class CopySet {
    Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onMouseButton(final MouseEvent event) {
        if (null == this.mc.theWorld || null == this.mc.thePlayer) {
            return;
        }
        if (2 == event.button && mc.thePlayer.capabilities.isCreativeMode) {
            final MovingObjectPosition mop = mc.objectMouseOver;
            if (MovingObjectPosition.MovingObjectType.ENTITY == mop.typeOfHit) {
                event.setCancelled(true);
                final Entity e = mop.entityHit;
                if (!(e instanceof EntityLivingBase)) {
                    return;
                }
                final EntityLivingBase entity = (EntityLivingBase) e;
                if (mc.thePlayer.isSneaking()) {
                    spawnItem(entity.getEquipmentInSlot(1), 5);
                    spawnItem(entity.getEquipmentInSlot(2), 6);
                    spawnItem(entity.getEquipmentInSlot(3), 7);
                    spawnItem(entity.getEquipmentInSlot(4), 3);
                } else {
                    HousingUtils.spawnItem(entity.getHeldItem());
                }
                Entry.playSound("mob.horse.armor");
            }
        }
    }

    private void spawnItem(final ItemStack item, final int slot) {
        mc.thePlayer.sendQueue.addToSendQueue(new C10PacketCreativeInventoryAction(slot, item));
    }
}

