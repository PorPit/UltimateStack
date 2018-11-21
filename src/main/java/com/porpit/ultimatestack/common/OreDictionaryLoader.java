package com.porpit.ultimatestack.common;

import com.porpit.ultimatestack.UltimateStack;
import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryLoader {
    public OreDictionaryLoader(FMLInitializationEvent event) {
        ConfigLoader.getCustomOreData().forEach((key, value) ->
                {
                    value.forEach(itemStack ->
                    {
                        OreDictionary.registerOre(key, itemStack);
                        UltimateStack.logger.info("注册自定义矿典- 矿典:"+key+" - ItemStack:"+itemStack.getItem().getRegistryName()+":"+itemStack.getMetadata());
                    });
                }
        );

    }
}
