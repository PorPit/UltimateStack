package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import cpw.mods.ironchest.common.tileentity.shulker.TileEntityIronShulkerBox;
import net.minecraft.tileentity.TileEntityLockableLoot;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TileEntityIronShulkerBox.class)
public abstract class MixinTileEntityIronShulkerBox extends TileEntityLockableLoot {
    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
