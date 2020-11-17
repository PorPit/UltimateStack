package com.porpit.ultimatestack.core.mixins;

import cofh.core.gui.container.InventoryContainerItemWrapper;
import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.inventory.IInventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InventoryContainerItemWrapper.class)
public abstract class MixinInventoryContainerItemWrapper implements IInventory {
    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
