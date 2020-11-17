package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import cpw.mods.ironchest.common.tileentity.chest.TileEntityIronChest;
import net.minecraft.tileentity.TileEntityLockableLoot;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TileEntityIronChest.class)
public abstract class MixinTileEntityIronChest extends TileEntityLockableLoot {
    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
