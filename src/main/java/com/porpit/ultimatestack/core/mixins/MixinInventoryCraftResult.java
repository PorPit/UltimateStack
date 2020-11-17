package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InventoryCraftResult.class)
public abstract class MixinInventoryCraftResult implements IInventory {
    @Override
    public int getInventoryStackLimit() {
        return ConfigLoader.MAX_STACK_SIZE;
    }
}