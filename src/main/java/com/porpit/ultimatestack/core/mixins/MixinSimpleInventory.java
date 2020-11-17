package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.inventory.IInventory;
import org.cyclops.cyclopscore.inventory.SimpleInventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SimpleInventory.class)
public abstract class MixinSimpleInventory implements IInventory {
    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
