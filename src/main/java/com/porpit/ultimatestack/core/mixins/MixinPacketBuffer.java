package com.porpit.ultimatestack.core.mixins;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(PacketBuffer.class)
public abstract class MixinPacketBuffer extends ByteBuf {
//    @Shadow(aliases = {"this$0"})
//    private PacketBuffer myOuter;

    @Shadow
    public abstract NBTTagCompound readCompoundTag();
    @Shadow
    public abstract PacketBuffer writeCompoundTag(@Nullable NBTTagCompound nbt);

    @Shadow public abstract int readInt();

    @Shadow public abstract ByteBuf writeInt(int p_writeInt_1_);

    @Inject(method = "readItemStack", at = @At("HEAD"), cancellable = true)
    public void mixinReadItemStack(CallbackInfoReturnable<ItemStack> ci){
        int i = this.readShort();

        if (i < 0)
        {
            ci.setReturnValue(ItemStack.EMPTY);
        }
        else
        {
            int j = this.readByte();
            if(j==-8){
                j=this.readInt();
            }
            int k = this.readShort();
            ItemStack itemstack = new ItemStack(Item.getItemById(i), j, k);
            itemstack.setTagCompound(this.readCompoundTag());
            ci.setReturnValue(itemstack);
        }
        ci.cancel();
    }

    @Inject(method = "writeItemStack", at = @At("HEAD"), cancellable = true)
    public void mixinWriteItemStack(ItemStack stack,CallbackInfoReturnable<PacketBuffer> ci){
        if (stack.isEmpty())
        {
            this.writeShort(-1);
        }
        else
        {
            //System.out.println("writeItem"+stack);

            this.writeShort(Item.getIdFromItem(stack.getItem()));
            if (stack.getCount()>0&&stack.getCount()<=Byte.MAX_VALUE) {
                this.writeByte(stack.getCount());
            }else {
                this.writeByte(-8);
                this.writeInt(stack.getCount());
            }


            this.writeShort(stack.getMetadata());
            NBTTagCompound nbttagcompound = null;

            if (stack.getItem().isDamageable() || stack.getItem().getShareTag())
            {
                nbttagcompound = stack.getItem().getNBTShareTag(stack);
            }

            this.writeCompoundTag(nbttagcompound);
        }

        ci.setReturnValue((PacketBuffer)(Object)this);
    }
}
