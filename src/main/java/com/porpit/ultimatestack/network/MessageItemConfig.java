package com.porpit.ultimatestack.network;

import com.porpit.ultimatestack.common.config.ConfigLoader;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
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
            short nameLength = buf.readShort();
            byte nameBytes[] = new byte[nameLength];
            buf.readBytes(nameBytes);
            String name = new String(nameBytes);
            itemMaxStackSizeMap.put(name, buf.readShort());
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(itemMaxStackSizeMap.size());
        itemMaxStackSizeMap.forEach((key, value) ->
        {
            byte[] nameBytes = key.getBytes();
            buf.writeShort(nameBytes.length);
            buf.writeBytes(nameBytes);
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
