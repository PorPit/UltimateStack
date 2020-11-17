package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import noppes.npcs.containers.SlotNpcBankCurrency;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SlotNpcBankCurrency.class)
public abstract class MixinSlotNpcBankCurrency extends Slot {
    public MixinSlotNpcBankCurrency(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public int getSlotStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
