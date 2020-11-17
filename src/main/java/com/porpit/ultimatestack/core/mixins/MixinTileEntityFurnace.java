package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityLockable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TileEntityFurnace.class)
public abstract class MixinTileEntityFurnace extends TileEntityLockable {

    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
