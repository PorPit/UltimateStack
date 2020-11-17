package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.inventory.IInventory;
import org.cyclops.cyclopscore.tileentity.InventoryTileEntityBase;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InventoryTileEntityBase.class)
public abstract class MixinInventoryTileEntityBase implements IInventory {
    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
