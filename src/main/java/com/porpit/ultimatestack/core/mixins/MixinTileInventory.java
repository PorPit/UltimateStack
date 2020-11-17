package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.mantle.tileentity.TileInventory;

@Mixin(TileInventory.class)
public class MixinTileInventory {
    @Shadow(remap=false)
    protected int stackSizeLimit;

    @Inject(method="<init>", at = @At("RETURN"), cancellable = true,remap=false)
    public void mixinInit( CallbackInfo ci){
        this.stackSizeLimit= ConfigLoader.MAX_STACK_SIZE;
    }
}
