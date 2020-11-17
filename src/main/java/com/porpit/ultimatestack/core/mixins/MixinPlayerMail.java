package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.inventory.IInventory;
import noppes.npcs.controllers.data.PlayerMail;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerMail.class)
public abstract class MixinPlayerMail implements IInventory {

    @Override
    public int getInventoryStackLimit(){
        return ConfigLoader.MAX_STACK_SIZE;
    }
}
