package com.porpit.ultimatestack.core;


import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.SortingIndex(1)
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class UltimateStackFMLLoadingPlugin implements IFMLLoadingPlugin{
    public static final Logger logger = LogManager.getLogger("UltimateStack");
    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return UltimateStackMixinFMLSetupHook.class.getName();
    }


    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
