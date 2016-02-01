package com.dynious.soundscool.network.packet.server;

import io.netty.buffer.ByteBuf;

import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.sound.Sound;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SoundRemovedPacket implements IMessage
{
    String soundName, category;

    public SoundRemovedPacket()
    {
    }

    public SoundRemovedPacket(String soundName, String category)
    {
        this.soundName = soundName;
        this.category = category;
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
        
        int catLength = bytes.readInt();
        char[] catCars = new char[catLength];
        for (int i = 0; i < catLength; i++)
        {
            catCars[i] = bytes.readChar();
        }
        category = String.valueOf(catCars);

        Sound sound = SoundHandler.getSound(soundName, category);
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
        
        bytes.writeInt(category.length());
        for (char c : category.toCharArray())
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
