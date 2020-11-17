package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.inventory.IInventory;
import noppes.npcs.containers.InventoryNPC;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InventoryNPC.class)
public abstract class MixinInventoryNPC implements IInventory {

    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
