package com.porpit.ultimatestack.common.event;

import com.porpit.ultimatestack.UltimateStack;
import com.porpit.ultimatestack.common.config.ConfigLoader;
import com.porpit.ultimatestack.network.MessageItemConfig;
import com.porpit.ultimatestack.network.NetworkLoader;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.oredict.OreDictionary;

public class EventLoader {
    public EventLoader()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        if(event.player.getEntityWorld().isRemote){
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP)event.player;
        MessageItemConfig message=new  MessageItemConfig(ConfigLoader.itemMaxStackSizeMap);
        UltimateStack.logger.debug("发送服务器config");
        NetworkLoader.instance.sendTo(message,player);
    }
    @SubscribeEvent
    public void onClientSideDisconnect( FMLNetworkEvent.ClientDisconnectionFromServerEvent event){
        ConfigLoader.isServerSuport=false;
    }

}
