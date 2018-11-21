package com.porpit.ultimatestack.network;

import com.porpit.ultimatestack.config.ConfigLoader;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;

public class MessageItemConfig implements IMessage {

    Map<String, Short> itemMaxStackSizeMap;

    public MessageItemConfig() {
    }

    public MessageItemConfig(Map<String, Short> itemMaxStackSizeMap) {
        this.itemMaxStackSizeMap = itemMaxStackSizeMap;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int count = buf.readInt();
        itemMaxStackSizeMap = new HashMap<>();
        for (int i = 0; i < count; i++) {
            int itemID=buf.readInt();
            int metaData=buf.readInt();
            short maxSize=buf.readShort();
            itemMaxStackSizeMap.put(Item.getItemById(itemID).getRegistryName()+":"+metaData, maxSize) ;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(itemMaxStackSizeMap.size());
        itemMaxStackSizeMap.forEach((key, value) ->
        {
            String[] nameAndMeta= key.split(":");
            String itemID=nameAndMeta[0];
            for(int i=1;i<nameAndMeta.length-1;i++){
                itemID+=(":"+nameAndMeta[i]);
            }
            int metaData= Integer.valueOf(nameAndMeta[nameAndMeta.length-1]);


            int itemIntID= Item.getIdFromItem(Item.getByNameOrId(itemID));

            buf.writeInt(itemIntID);
            buf.writeInt(metaData);
            buf.writeShort(value);
        });
    }

    public static class Handler implements IMessageHandler<MessageItemConfig, IMessage> {
        @Override
        public IMessage onMessage(MessageItemConfig message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    ConfigLoader.loadItemDataFormServer(message.itemMaxStackSizeMap);
                });
                System.out.println("test");
                ConfigLoader.isServerSuport=true;
            }

            return null;
        }
    }
}
