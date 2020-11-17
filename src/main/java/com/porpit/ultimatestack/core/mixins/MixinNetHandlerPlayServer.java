package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer {

    @Shadow
    public EntityPlayerMP player;
    @Shadow

    private int itemDropThreshold;
    @Inject(method = "processCreativeInventoryAction",at=@At("HEAD"),cancellable = true)
    public void mixinProcessCreativeInventoryAction(CPacketCreativeInventoryAction packetIn, CallbackInfo ci){
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, (NetHandlerPlayServer)(Object)this, this.player.getServerWorld());

        if (this.player.interactionManager.isCreative())
        {
            boolean flag = packetIn.getSlotId() < 0;
            ItemStack itemstack = packetIn.getStack();

            if (!itemstack.isEmpty() && itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("BlockEntityTag", 10))
            {
                NBTTagCompound nbttagcompound = itemstack.getTagCompound().getCompoundTag("BlockEntityTag");

                if (nbttagcompound.hasKey("x") && nbttagcompound.hasKey("y") && nbttagcompound.hasKey("z"))
                {
                    BlockPos blockpos = new BlockPos(nbttagcompound.getInteger("x"), nbttagcompound.getInteger("y"), nbttagcompound.getInteger("z"));
                    TileEntity tileentity = this.player.world.getTileEntity(blockpos);

                    if (tileentity != null)
                    {
                        NBTTagCompound nbttagcompound1 = tileentity.writeToNBT(new NBTTagCompound());
                        nbttagcompound1.removeTag("x");
                        nbttagcompound1.removeTag("y");
                        nbttagcompound1.removeTag("z");
                        itemstack.setTagInfo("BlockEntityTag", nbttagcompound1);
                    }
                }
            }

            boolean flag1 = packetIn.getSlotId() >= 1 && packetIn.getSlotId() <= 45;
            boolean flag2 = itemstack.isEmpty() || itemstack.getMetadata() >= 0 && itemstack.getCount() <= ConfigLoader.MAX_STACK_SIZE && !itemstack.isEmpty();

            if (flag1 && flag2)
            {
                if (itemstack.isEmpty())
                {
                    this.player.inventoryContainer.putStackInSlot(packetIn.getSlotId(), ItemStack.EMPTY);
                }
                else
                {
                    this.player.inventoryContainer.putStackInSlot(packetIn.getSlotId(), itemstack);
                }

                this.player.inventoryContainer.setCanCraft(this.player, true);
            }
            else if (flag && flag2 && this.itemDropThreshold < 200)
            {
                this.itemDropThreshold += 20;
                EntityItem entityitem = this.player.dropItem(itemstack, true);

                if (entityitem != null)
                {
                    entityitem.setAgeToCreativeDespawnTime();
                }
            }
        }
        ci.cancel();
    }
}
