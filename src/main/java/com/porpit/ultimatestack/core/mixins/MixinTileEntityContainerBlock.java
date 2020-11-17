package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import mekanism.common.tile.prefab.TileEntityContainerBlock;
import net.minecraft.inventory.ISidedInventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TileEntityContainerBlock.class)
public abstract class MixinTileEntityContainerBlock  implements ISidedInventory {

    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
