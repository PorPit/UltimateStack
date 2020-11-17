package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityLockable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TileEntityBrewingStand.class)
public abstract class MixinTileEntityBrewingStand extends TileEntityLockable {
    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
