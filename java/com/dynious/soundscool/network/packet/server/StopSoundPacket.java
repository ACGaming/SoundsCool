package com.dynious.soundscool.network.packet.server;

import io.netty.buffer.ByteBuf;

import com.dynious.soundscool.client.audio.SoundPlayer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class StopSoundPacket implements IMessage
{
    String identifier;
    public StopSoundPacket()
    {
    }

    public StopSoundPacket(String identifier)
    {
        this.identifier = identifier;
    }

    @Override
    public void fromBytes(ByteBuf bytes)
    {
        int soundLength = bytes.readInt();
        char[] fileCars = new char[soundLength];
        for (int i = 0; i < soundLength; i++)
        {
            fileCars[i] = bytes.readChar();
        }
        identifier = String.valueOf(fileCars);

        SoundPlayer.stopSound(identifier);
    }

    @Override
    public void toBytes(ByteBuf bytes)
    {
        bytes.writeInt(identifier.length());
        for (char c : identifier.toCharArray())
        {
            bytes.writeChar(c);
        }
    }
    
    public static class Handler implements IMessageHandler<StopSoundPacket, IMessage> {
        @Override
        public IMessage onMessage(StopSoundPacket message, MessageContext ctx) {
            return null;
        }
    }
}
