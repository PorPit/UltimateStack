package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.PacketUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PacketUtil.class)
public abstract class MixinPacketUtil {
    @Inject(method = "writeItemStackFromClientToServer", at = @At("HEAD"), cancellable = true,remap = false)
    private static void mixinWriteItemStackFromClientToServer(PacketBuffer buffer, ItemStack stack, CallbackInfo ci){
        if (stack.isEmpty())
        {
            buffer.writeShort(-1);
        }
        else
        {
            //System.out.println("write to Server"+stack);
            buffer.writeShort(Item.getIdFromItem(stack.getItem()));
            if (ConfigLoader.isServerSuport) {
                if (stack.getCount()>0&&stack.getCount()<=Byte.MAX_VALUE) {
                    buffer.writeByte(stack.getCount());
                } else {
                    buffer.writeByte(-8);
                    buffer.writeInt(stack.getCount());
                }
            }else {
                buffer.writeByte(stack.getCount());
            }

            buffer.writeShort(stack.getMetadata());
            NBTTagCompound nbttagcompound = null;

            if (stack.getItem().isDamageable() || stack.getItem().getShareTag())
            {
                nbttagcompound = stack.getTagCompound();
            }

            buffer.writeCompoundTag(nbttagcompound);
        }
        ci.cancel();
    }
}
