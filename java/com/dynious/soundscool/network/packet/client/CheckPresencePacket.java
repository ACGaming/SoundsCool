package com.dynious.soundscool.network.packet.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.helper.NetworkHelper;
import com.dynious.soundscool.network.packet.server.SoundNotFoundPacket;
import com.dynious.soundscool.sound.Sound;

public class CheckPresencePacket implements IMessage
{
    String fileName, category;
    int entityID;
    int worldID;

    public CheckPresencePacket()
    {
    }

    public CheckPresencePacket(String soundName, String category)
    {
        this.fileName = soundName;
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
        fileName = String.valueOf(fileCars);
        
        int catLength = bytes.readInt();
        char[] catCars = new char[catLength];
        for (int i = 0; i < catLength; i++)
        {
            catCars[i] = bytes.readChar();
        }
        category = String.valueOf(catCars);
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
    }
    
    public static class Handler implements IMessageHandler<CheckPresencePacket, IMessage> {
        @Override
        public IMessage onMessage(CheckPresencePacket message, MessageContext ctx) 
        {  	
        	EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player != null)
            {
                Sound sound = SoundHandler.getSound(message.fileName, message.category);

                if (sound != null)
                {
                    NetworkHelper.serverSoundUpload(sound, player);
                }
                else
                {
                	return new SoundNotFoundPacket(message.fileName);
                }
            }
            return null;
        }
    }
}
