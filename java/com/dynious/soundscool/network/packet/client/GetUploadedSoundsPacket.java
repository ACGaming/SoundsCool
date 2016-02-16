package com.dynious.soundscool.network.packet.client;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.network.packet.server.UploadedSoundsPacket;

public class GetUploadedSoundsPacket implements IMessage
{
    int entityID;
    int worldID;

    public GetUploadedSoundsPacket()
    {
    }

    @Override
    public void fromBytes(ByteBuf bytes)
    {
    }

    @Override
    public void toBytes(ByteBuf bytes)
    {
    }
    
    public static class Handler implements IMessageHandler<GetUploadedSoundsPacket, IMessage> {
        @Override
        public IMessage onMessage(GetUploadedSoundsPacket message, MessageContext ctx) {
        	SoundHandler.findSounds();
            return new UploadedSoundsPacket();
        }
    }
}
