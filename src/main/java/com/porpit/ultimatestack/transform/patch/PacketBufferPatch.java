package com.porpit.ultimatestack.transform.patch;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class PacketBufferPatch extends PacketBuffer {
    public PacketBufferPatch(ByteBuf wrapped) {
        super(wrapped);
    }

    @Override
    public PacketBuffer writeItemStack(ItemStack stack){
        if (stack.isEmpty())
        {
            this.writeShort(-1);
        }
        else
        {
            System.out.println("writeItem"+stack);

            this.writeShort(Item.getIdFromItem(stack.getItem()));
            if (stack.getCount()>0&&stack.getCount()<=Byte.MAX_VALUE) {
                this.writeByte(stack.getCount());
            }else {
                this.writeByte(-8);
                this.writeShort(stack.getCount());
            }


            this.writeShort(stack.getMetadata());
            NBTTagCompound nbttagcompound = null;

            if (stack.getItem().isDamageable() || stack.getItem().getShareTag())
            {
                nbttagcompound = stack.getItem().getNBTShareTag(stack);
            }

            this.writeCompoundTag(nbttagcompound);
        }

        return this;
    }

    @Override
    public ItemStack readItemStack() throws IOException {

        int i = this.readShort();

        if (i < 0)
        {
            return ItemStack.EMPTY;
        }
        else
        {
            System.out.println("loadItem");
            int j = this.readByte();
            if(j==-8){
                j=this.readShort();
            }
            int k = this.readShort();
            ItemStack itemstack = new ItemStack(Item.getItemById(i), j, k);
            itemstack.setTagCompound(this.readCompoundTag());
            return itemstack;
        }
    }
}
