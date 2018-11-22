package com.porpit.ultimatestack.common;

import com.porpit.ultimatestack.config.ConfigLoader;
import com.porpit.ultimatestack.common.event.EventLoader;
import com.porpit.ultimatestack.item.ItemLoader;
import com.porpit.ultimatestack.network.NetworkLoader;
import com.porpit.ultimatestack.network.version.VersionChecker;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        new ConfigLoader(event);
        new NetworkLoader(event);
        new EventLoader();
        new ItemLoader(event);
        new VersionChecker();
    }

    public void init(FMLInitializationEvent event) {
        new OreDictionaryLoader(event);
        ConfigLoader.loadItemData();

    }

    public void postInit(FMLPostInitializationEvent event) {
    }

    public void serverStarting(FMLServerStartingEvent event) {

    }
}
