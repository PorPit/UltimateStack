package com.porpit.ultimatestack.transform.patch;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class PacketUtilPatch {
    public static void writeItemStackFromClientToServer(PacketBuffer buffer, ItemStack stack)
    {
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
                    buffer.writeShort(stack.getCount());
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
    }
}
