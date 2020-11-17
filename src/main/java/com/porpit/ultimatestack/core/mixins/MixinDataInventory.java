package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.inventory.IInventory;
import noppes.npcs.entity.data.DataInventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DataInventory.class)
public abstract class MixinDataInventory implements IInventory {
    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }

}
