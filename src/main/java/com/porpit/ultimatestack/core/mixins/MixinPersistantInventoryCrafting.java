package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import micdoodle8.mods.galacticraft.core.inventory.PersistantInventoryCrafting;
import net.minecraft.inventory.IInventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PersistantInventoryCrafting.class)
public abstract class MixinPersistantInventoryCrafting implements IInventory {
    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
