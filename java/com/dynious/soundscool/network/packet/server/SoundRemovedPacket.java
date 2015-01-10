package com.dynious.soundscool.network.packet.server;

import io.netty.buffer.ByteBuf;

import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.sound.Sound;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SoundRemovedPacket implements IMessage
{
    String soundName;

    public SoundRemovedPacket()
    {
    }

    public SoundRemovedPacket(String soundName)
    {
        this.soundName = soundName;
    }

    @Override
    public void fromBytes(ByteBuf bytes)
    {
        int fileLength = bytes.readInt();
        char[] fileCars = new char[fileLength];
        for (int i = 0; i < fileLength; i++)
        {
            fileCars[i] = bytes.readChar();
        }
        soundName = String.valueOf(fileCars);

        Sound sound = SoundHandler.getSound(soundName);
        if (sound != null)
        {
            SoundHandler.remoteRemovedSound(sound);
        }
    }

    @Override
    public void toBytes(ByteBuf bytes)
    {
        bytes.writeInt(soundName.length());
        for (char c : soundName.toCharArray())
        {
            bytes.writeChar(c);
        }
    }
    
    public static class Handler implements IMessageHandler<SoundRemovedPacket, IMessage> {
        @Override
        public IMessage onMessage(SoundRemovedPacket message, MessageContext ctx) {
            return null;
        }
    }
}
