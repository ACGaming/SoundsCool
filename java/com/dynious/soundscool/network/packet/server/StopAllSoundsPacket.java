package com.dynious.soundscool.network.packet.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.dynious.soundscool.client.audio.SoundPlayer;

public class StopAllSoundsPacket implements IMessage 
{
	public StopAllSoundsPacket()
	{
	}

	@Override
	public void fromBytes(ByteBuf bytes)
	{
		SoundPlayer.getInstance().stopSounds();
	}

	@Override
	public void toBytes(ByteBuf bytes)
	{
		
	}

	public static class Handler implements IMessageHandler<StopAllSoundsPacket, IMessage> {
		@Override
		public IMessage onMessage(StopAllSoundsPacket message, MessageContext ctx) {
			return null;
		}
	}
}
