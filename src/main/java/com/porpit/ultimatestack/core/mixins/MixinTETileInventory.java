package com.porpit.ultimatestack.core.mixins;

import cofh.thermalexpansion.block.TileInventory;
import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.inventory.IInventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TileInventory.class)
public abstract class MixinTETileInventory implements IInventory {
    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
