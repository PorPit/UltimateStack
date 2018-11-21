package com.porpit.ultimatestack;

import com.porpit.ultimatestack.common.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

@Mod(modid = UltimateStack.MODID, name = UltimateStack.NAME, version = UltimateStack.VERSION,dependencies = "required-after:ppcore@[1.0.0,);", acceptedMinecraftVersions = "1.12.2")
public class UltimateStack {
    public static final String MODID = "ultimatestack";
    public static final String NAME = "UltimateStack";
    public static final String VERSION = "1.0.0";

    @SidedProxy(clientSide = "com.porpit.ultimatestack.client.ClientProxy", serverSide = "com.porpit.ultimatestack.common.CommonProxy")
    public static CommonProxy proxy;
    @Mod.Instance(UltimateStack.MODID)
    public static UltimateStack instance;

    public static Logger logger;

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger=event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) {

    }
}
