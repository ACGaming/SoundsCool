package com.dynious.soundscool.network.packet.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.sound.Sound;

public class UploadedSoundsPacket implements IMessage
{
	private int start, finish;
	
    public UploadedSoundsPacket()
    {
    }
    
    public UploadedSoundsPacket(int start, int finish)
    {
    	this.start = start;
    	this.finish = finish;
    }

    @Override
    public void fromBytes(ByteBuf bytes)
    {
    	SoundHandler.serverListSize = bytes.readInt();
        this.start = bytes.readInt();
        this.finish = bytes.readInt();
        
        SoundHandler.guiRemoteList.clear();
        for (int y = start; y < finish; y++)
        {
            int soundNameLength = bytes.readInt();
            char[] soundNameCars = new char[soundNameLength];
            for (int i = 0; i < soundNameLength; i++)
            {
                soundNameCars[i] = bytes.readChar();
            }
            int soundCatLength = bytes.readInt();
            char[] soundCatChars = new char[soundCatLength];
            for (int i = 0; i < soundCatLength; i++)
            {
                soundCatChars[i] = bytes.readChar();
            }
            SoundHandler.addRemoteSound(String.valueOf(soundNameCars), String.valueOf(soundCatChars));
            SoundHandler.guiRemoteList.add(new Sound(String.valueOf(soundNameCars), String.valueOf(soundCatChars)));
        }
    }

    @Override
    public void toBytes(ByteBuf bytes)
    {
    	int size = SoundHandler.getSounds().size();
    	bytes.writeInt(size);
        bytes.writeInt(start);
        finish = finish < size ? finish : size-1;
        bytes.writeInt(finish);
        for (int i=start; i<finish; i++)
        {
        	Sound sound = SoundHandler.getSounds().get(i);
            bytes.writeInt(sound.getSoundName().length());
            for (char c : sound.getSoundName().toCharArray())
            {
                bytes.writeChar(c);
            }
            bytes.writeInt(sound.getCategory().length());
            for (char c : sound.getCategory().toCharArray())
            {
                bytes.writeChar(c);
            }
        }
    }
    
    public static class Handler implements IMessageHandler<UploadedSoundsPacket, IMessage> {
        @Override
        public IMessage onMessage(UploadedSoundsPacket message, MessageContext ctx) {
            return null;
        }
    }
}
