package com.porpit.ultimatestack;

import com.google.inject.Inject;
import com.porpit.ultimatestack.common.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

@Mod(modid = UltimateStack.MODID, name = UltimateStack.NAME, version = UltimateStack.VERSION, dependencies = "required-after:ppcore@[%PPCoreVersion%,);", acceptedMinecraftVersions = "1.12.2")
public class UltimateStack {
    public static final String MODID = "ultimatestack";
    public static final String NAME = "UltimateStack";
    public static final String VERSION = "%UltimateStackVersion%";

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
        logger = event.getModLog();
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
        logger.info("UltimateStack (终极堆叠) MOD 成功加载! 作者:PorPit(泼皮)");
        logger.info("如有问题反馈 https://github.com/PorPit/UltimateStack/issues   泼皮QQ:692066768");
    }
}
