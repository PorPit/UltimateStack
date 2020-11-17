package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import noppes.npcs.NpcMiscInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NpcMiscInventory.class)
public abstract class MixinNpcMiscInventory {
    @Shadow(remap = false)
    public int stackLimit;
    @Inject(method="<init>", at = @At("RETURN"), cancellable = true,remap = false)
    public void mixinInit(CallbackInfo ci){
        this.stackLimit=ConfigLoader.MAX_STACK_SIZE;
    }
}
