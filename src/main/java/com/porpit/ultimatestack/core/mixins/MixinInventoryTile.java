package com.porpit.ultimatestack.core.mixins;

import blusunrize.immersiveengineering.common.gui.InventoryTile;
import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.inventory.IInventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InventoryTile.class)
public abstract class MixinInventoryTile implements IInventory {
    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
