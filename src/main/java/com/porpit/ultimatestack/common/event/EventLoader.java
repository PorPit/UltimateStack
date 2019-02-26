package com.porpit.ultimatestack.common.event;

import com.porpit.ppcore.util.VersionCompare;
import com.porpit.ultimatestack.UltimateStack;
import com.porpit.ultimatestack.config.ConfigLoader;
import com.porpit.ultimatestack.network.MessageItemConfig;
import com.porpit.ultimatestack.network.NetworkLoader;
import com.porpit.ultimatestack.network.version.VersionChecker;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

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

        TextComponentTranslation text1=new TextComponentTranslation("§9§l[UltimateStack] §3终极堆叠模组  当前版本:§a "+UltimateStack.VERSION.replace("%","" ));
        text1.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, VersionChecker.downloadUrl));
        player.sendMessage(text1);
        if(VersionCompare.compareVersion(UltimateStack.VERSION,VersionChecker.newerModVersion )==-1){
            TextComponentTranslation text2=new TextComponentTranslation("§9§l[UltimateStack] §3检测到新版本:§a "+VersionChecker.newerModVersion+" ,§n点击查看");
            text2.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/PorPit/UltimateStack/releases"));
            player.sendMessage(text2);
        }
    }
    @SubscribeEvent
    public void onClientSideDisconnect( FMLNetworkEvent.ClientDisconnectionFromServerEvent event){
        ConfigLoader.isServerSuport=false;
    }

}
