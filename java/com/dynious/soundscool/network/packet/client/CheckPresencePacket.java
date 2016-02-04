package com.dynious.soundscool.network.packet.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.dynious.soundscool.SoundsCool;
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

    public CheckPresencePacket(String soundName, String category, EntityPlayer player)
    {
        this.fileName = soundName;
        this.category = category;
        this.entityID = player.getEntityId();
        this.worldID = player.getEntityWorld().provider.getDimensionId();
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
        
        entityID = bytes.readInt();
        worldID = bytes.readInt();

        Entity entity = DimensionManager.getWorld(worldID).getEntityByID(entityID);
        if (entity != null && entity instanceof EntityPlayer)
        {
            Sound sound = SoundHandler.getSound(fileName, category);

            if (sound != null)
            {
                NetworkHelper.serverSoundUpload(sound, (EntityPlayerMP) entity);
            }
            else
            {
                SoundsCool.network.sendTo(new SoundNotFoundPacket(fileName), (EntityPlayerMP)entity);
            }
        }
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
        
        bytes.writeInt(entityID);
        bytes.writeInt(worldID);
    }
    
    public static class Handler implements IMessageHandler<CheckPresencePacket, IMessage> {
        @Override
        public IMessage onMessage(CheckPresencePacket message, MessageContext ctx) {
            return null;
        }
    }
}
