package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.entity.item.EntityMinecartContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityMinecartContainer.class)
public abstract class MixinEntityMinecartContainer{
    @Inject(method = "getInventoryStackLimit", at = @At("HEAD"), cancellable = true)
    protected void onGetPos(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ConfigLoader.MAX_STACK_SIZE);
    }
}
