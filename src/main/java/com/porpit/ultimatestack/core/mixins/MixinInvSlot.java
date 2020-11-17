package com.porpit.ultimatestack.core.mixins;

import com.porpit.ultimatestack.config.ConfigLoader;
import ic2.core.block.invslot.InvSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InvSlot.class)
public abstract class MixinInvSlot  implements Iterable<ItemStack>{
    @Shadow(remap = false)
    private int stackSizeLimit;
    @Inject(method = "getStackSizeLimit", at = @At("HEAD"), cancellable = true ,remap=false)
    protected void onGetPos(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ConfigLoader.MAX_STACK_SIZE);
    }
}
