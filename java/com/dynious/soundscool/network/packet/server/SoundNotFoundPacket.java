package com.dynious.soundscool.network.packet.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.dynious.soundscool.handler.DelayedPlayHandler;
import com.dynious.soundscool.sound.SoundInfo;

public class SoundNotFoundPacket implements IMessage
{
    String soundName, category;
    public SoundNotFoundPacket()
    {
    }

    public SoundNotFoundPacket(SoundInfo soundInfo)
    {
        this.soundName = soundInfo.name;
        this.category = soundInfo.category;
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
        
        DelayedPlayHandler.removeSound(new SoundInfo(soundName, category));
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
    
    public static class Handler implements IMessageHandler<SoundNotFoundPacket, IMessage> {
        @Override
        public IMessage onMessage(SoundNotFoundPacket message, MessageContext ctx) {
            return null;
        }
    }
}
