package com.dynious.soundscool.network.packet;

import io.netty.buffer.ByteBuf;

import java.io.File;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.dynious.soundscool.SoundsCool;
import com.dynious.soundscool.handler.DelayedPlayHandler;
import com.dynious.soundscool.handler.NetworkHandler;
import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.helper.NetworkHelper;
import com.dynious.soundscool.network.packet.server.SoundReceivedPacket;
import com.dynious.soundscool.sound.Sound;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SoundUploadedPacket implements IMessage
{
    String category;
    String soundName;
    public SoundUploadedPacket()
    {
    }

    public SoundUploadedPacket(String soundName, String category)
    {
        this.category = category;
        this.soundName = soundName;
    }

    @Override
    public void fromBytes(ByteBuf bytes)
    {
        int catLength = bytes.readInt();
        char[] catCars = new char[catLength];
        for (int i = 0; i < catLength; i++)
        {
            catCars[i] = bytes.readChar();
        }
        category = String.valueOf(catCars);

        int fileLength = bytes.readInt();
        char[] fileCars = new char[fileLength];
        for (int i = 0; i < fileLength; i++)
        {
            fileCars[i] = bytes.readChar();
        }
        soundName = String.valueOf(fileCars);

        File soundFile = NetworkHelper.createFileFromByteArr(NetworkHandler.soundUploaded(soundName), category, soundName);
        SoundHandler.addLocalSound(soundName, category, soundFile);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            DelayedPlayHandler.onSoundReceived(soundName, category);
        }
        else
        {
            EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(category);
            if (player != null)
            {
                SoundsCool.network.sendTo(new SoundReceivedPacket(SoundHandler.getSound(soundName, category)), player);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf bytes)
    {
        bytes.writeInt(category.length());
        for (char c : category.toCharArray())
        {
            bytes.writeChar(c);
        }
        bytes.writeInt(soundName.length());
        for (char c : soundName.toCharArray())
        {
            bytes.writeChar(c);
        }
    }
    
    public static class ServerSideHandler implements IMessageHandler<SoundUploadedPacket, IMessage> {
        @Override
        public IMessage onMessage(SoundUploadedPacket message, MessageContext ctx) {
        	EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        	int dimension = player.dimension;
        	TargetPoint targetPoint = new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 64);
        	SoundsCool.network.sendToAllAround(new SoundReceivedPacket(new Sound(message.soundName, message.category)), targetPoint);
            return null;
        }
    }
    
    public static class ClientSideHandler implements IMessageHandler<SoundUploadedPacket, IMessage> {
        @Override
        public IMessage onMessage(SoundUploadedPacket message, MessageContext ctx) {
            return null;
        }
    }
}
