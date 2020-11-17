package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import cubex2.mods.multipagechest.InventoryVoid;
import net.minecraft.inventory.IInventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InventoryVoid.class)
public abstract class MixinInventoryVoid implements IInventory {
    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
