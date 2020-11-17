package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryMerchant;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InventoryMerchant.class)
public abstract class MixinInventoryMerchant implements IInventory {
    @Override
    public int getInventoryStackLimit() {
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
