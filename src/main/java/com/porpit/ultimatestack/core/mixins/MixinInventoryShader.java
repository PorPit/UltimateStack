package com.porpit.ultimatestack.core.mixins;

import blusunrize.immersiveengineering.common.gui.InventoryShader;
import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.inventory.IInventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InventoryShader.class)
public abstract class MixinInventoryShader implements IInventory {
    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
