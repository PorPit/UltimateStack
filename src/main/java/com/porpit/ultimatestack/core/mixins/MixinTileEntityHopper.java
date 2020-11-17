package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityLockableLoot;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TileEntityHopper.class)
public abstract class MixinTileEntityHopper extends TileEntityLockableLoot {
    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
