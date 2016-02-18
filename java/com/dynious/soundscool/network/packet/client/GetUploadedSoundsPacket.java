package com.dynious.soundscool.network.packet.client;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.dynious.soundscool.network.packet.server.UploadedSoundsPacket;

public class GetUploadedSoundsPacket implements IMessage
{
	private int start, finish;

    public GetUploadedSoundsPacket()
    {
    }
    
    public GetUploadedSoundsPacket(int start, int finish)
    {
    	this.start = start;
    	this.finish = finish;
    }

    @Override
    public void fromBytes(ByteBuf bytes)
    {
    	start = bytes.readInt();
    	finish = bytes.readInt();
    }

    @Override
    public void toBytes(ByteBuf bytes)
    {
    	bytes.writeInt(start);
    	bytes.writeInt(finish);
    }
    
    public static class Handler implements IMessageHandler<GetUploadedSoundsPacket, IMessage> {
        @Override
        public IMessage onMessage(GetUploadedSoundsPacket message, MessageContext ctx) {
            return new UploadedSoundsPacket(message.start, message.finish);
        }
    }
}
