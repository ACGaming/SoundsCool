package com.dynious.soundscool.network.packet.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.helper.NetworkHelper;
import com.dynious.soundscool.network.packet.server.SoundNotFoundPacket;
import com.dynious.soundscool.network.packet.server.SoundReceivedPacket;
import com.dynious.soundscool.sound.Sound;
import com.dynious.soundscool.sound.SoundInfo;

public class CheckPresencePacket implements IMessage
{
    String fileName, category;
    boolean request;

    public CheckPresencePacket()
    {
    }

    public CheckPresencePacket(SoundInfo soundInfo, boolean request)
    {
        this.fileName = soundInfo.name;
        this.category = soundInfo.category;
        this.request = request;
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
        fileName = String.valueOf(fileCars);
        
        int catLength = bytes.readInt();
        char[] catCars = new char[catLength];
        for (int i = 0; i < catLength; i++)
        {
            catCars[i] = bytes.readChar();
        }
        category = String.valueOf(catCars);
        
        request = bytes.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf bytes)
    {
        bytes.writeInt(fileName.length());
        for (char c : fileName.toCharArray())
        {
            bytes.writeChar(c);
        }
        
        bytes.writeInt(category.length());
        for (char c : category.toCharArray())
        {
            bytes.writeChar(c);
        }
        
        bytes.writeBoolean(request);
    }
    
    public static class Handler implements IMessageHandler<CheckPresencePacket, IMessage> {
        @Override
        public IMessage onMessage(CheckPresencePacket message, MessageContext ctx) 
        {  	
        	EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player != null)
            {
            	SoundInfo soundInfo = new SoundInfo(message.fileName, message.category);
                Sound sound = SoundHandler.getSound(soundInfo);

                if (sound != null && message.request)
                {
                    NetworkHelper.serverSoundUpload(sound, player);
                }
                else if(sound != null)
                {
                	return new SoundReceivedPacket(soundInfo);
                }
                else 
                {
                	return new SoundNotFoundPacket(soundInfo);
                }
            }
            return null;
        }
    }
}
