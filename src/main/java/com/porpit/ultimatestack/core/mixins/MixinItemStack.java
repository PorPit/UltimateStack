package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements net.minecraftforge.common.capabilities.ICapabilitySerializable<NBTTagCompound>{
    @Shadow
    private Item item;
//    @Shadow(aliases = {"this"})
//    private ItemStack myOuter;
    @Shadow
    private int stackSize;
    @Shadow
    int itemDamage;
    @Shadow
    private NBTTagCompound stackTagCompound;
    @Shadow
    private void updateEmptyState(){}
    @Shadow(remap = false)
    private net.minecraftforge.common.capabilities.CapabilityDispatcher capabilities;
    @Shadow(remap = false)
    protected abstract void forgeInit();

    @Inject(method = "getMaxStackSize", at = @At("HEAD"),cancellable = true)
    public void mixinGetMaxStackSize(CallbackInfoReturnable<Integer> ci){
        ci.setReturnValue(ConfigLoader.getMaxStackSizeSetting((ItemStack)(Object)this));
        ci.cancel();
    }
    @Inject(method="writeToNBT", at = @At("RETURN"), cancellable = true)
    public void mixinWriteToNBT(NBTTagCompound nbt,CallbackInfoReturnable<NBTTagCompound> ci){
        ResourceLocation resourcelocation = Item.REGISTRY.getNameForObject(this.item);
        nbt.setString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
        nbt.setInteger("Count", this.stackSize);
        nbt.setShort("Damage", (short)this.itemDamage);

        if (this.stackTagCompound != null)
        {
            nbt.setTag("tag", this.stackTagCompound);
        }

        if (this.capabilities != null)
        {
            NBTTagCompound cnbt = this.capabilities.serializeNBT();
            if (!cnbt.hasNoTags()) nbt.setTag("ForgeCaps", cnbt);
        }
        ci.setReturnValue(nbt);
        ci.cancel();
    }
    @Inject(method="<init>(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("RETURN"), cancellable = true)
    public void mixinInit( NBTTagCompound compound,CallbackInfo ci){
        this.stackSize = compound.getInteger("Count");
        this.updateEmptyState();
        this.forgeInit();
    }
}
