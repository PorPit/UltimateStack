package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import ic2.core.block.TileEntityInventory;
import net.minecraft.inventory.IInventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TileEntityInventory.class)
public abstract class MixinICTileEntityInventory implements IInventory {
    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
