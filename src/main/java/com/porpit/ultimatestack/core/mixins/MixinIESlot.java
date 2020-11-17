package com.porpit.ultimatestack.core.mixins;

import blusunrize.immersiveengineering.common.gui.IESlot;
import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IESlot.class)
public abstract class MixinIESlot extends Slot {
    public MixinIESlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public int getSlotStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
