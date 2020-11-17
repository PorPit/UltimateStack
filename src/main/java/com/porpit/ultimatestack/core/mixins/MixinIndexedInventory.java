package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.inventory.IInventory;
import org.cyclops.cyclopscore.inventory.IndexedInventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IndexedInventory.class)
public abstract class MixinIndexedInventory implements IInventory {
    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
