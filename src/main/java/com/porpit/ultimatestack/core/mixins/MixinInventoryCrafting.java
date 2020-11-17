package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InventoryCrafting.class)
public abstract class MixinInventoryCrafting implements IInventory {
    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}