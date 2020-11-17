package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntityShulkerBox;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TileEntityShulkerBox.class)
public abstract class MixinTileEntityShulkerBox extends TileEntityLockableLoot {
    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
