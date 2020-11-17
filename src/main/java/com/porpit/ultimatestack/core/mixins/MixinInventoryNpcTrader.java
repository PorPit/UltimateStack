package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.inventory.IInventory;
import noppes.npcs.containers.InventoryNpcTrader;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InventoryNpcTrader.class)
public abstract class MixinInventoryNpcTrader implements IInventory {

    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
