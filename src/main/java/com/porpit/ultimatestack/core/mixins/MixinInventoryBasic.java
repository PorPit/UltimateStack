package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InventoryBasic.class)
public abstract class MixinInventoryBasic implements IInventory {
    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
