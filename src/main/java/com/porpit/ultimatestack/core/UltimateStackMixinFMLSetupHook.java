package com.porpit.ultimatestack.core;

import net.minecraftforge.fml.relauncher.IFMLCallHook;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public class UltimateStackMixinFMLSetupHook implements IFMLCallHook {

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public Void call() {
        UltimateStackFMLLoadingPlugin.logger.info("UltimateStack has been hooked by Forge, setting up Mixin and plugins");

        Mixins.addConfiguration(UltimateStackMixinPlugin.MIXIN_JSON);

        return null;
    }
}
