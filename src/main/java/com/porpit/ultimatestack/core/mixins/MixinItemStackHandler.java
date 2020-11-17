package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStackHandler.class)
public abstract class MixinItemStackHandler implements IItemHandler, IItemHandlerModifiable {
    @Override
    public int getSlotLimit(int slot)
    {
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
