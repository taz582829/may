package lol.ethan.may.housing;

import lol.ethan.may.Entry;
import lol.ethan.may.events.MouseEvent;
import lol.ethan.may.events.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.util.MovingObjectPosition;

public class CopySet {
    Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onMouseButton(MouseEvent event) {
        if (this.mc.theWorld == null || this.mc.thePlayer == null) {
            return;
        }
        if (event.button == 2 && this.mc.thePlayer.capabilities.isCreativeMode) {
            MovingObjectPosition mop = this.mc.objectMouseOver;
            if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                event.setCancelled(true);
                Entity e = mop.entityHit;
                if (!(e instanceof EntityLivingBase)) {
                    return;
                }
                EntityLivingBase entity = (EntityLivingBase) e;
                if (this.mc.thePlayer.isSneaking()) {
                    this.spawnItem(entity.getEquipmentInSlot(1), 5);
                    this.spawnItem(entity.getEquipmentInSlot(2), 6);
                    this.spawnItem(entity.getEquipmentInSlot(3), 7);
                    this.spawnItem(entity.getEquipmentInSlot(4), 3);
                } else {
                    HousingUtils.spawnItem(entity.getHeldItem());
                }
                Entry.playSound("mob.horse.armor");
            }
        }
    }

    private void spawnItem(ItemStack item, int slot) {
        this.mc.thePlayer.sendQueue.addToSendQueue(new C10PacketCreativeInventoryAction(slot, item));
    }
}

