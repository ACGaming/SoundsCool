package com.dynious.soundscool.network.packet;

import java.io.File;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.dynious.soundscool.SoundsCool;
import com.dynious.soundscool.handler.DelayedPlayHandler;
import com.dynious.soundscool.handler.NetworkHandler;
import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.helper.NetworkHelper;
import com.dynious.soundscool.network.packet.server.SoundReceivedPacket;
import com.dynious.soundscool.sound.Sound;
import com.dynious.soundscool.sound.SoundInfo;
import io.netty.buffer.ByteBuf;

public class SoundUploadedPacket implements IMessage
{
    String title, artist, fileName;

    public SoundUploadedPacket() {}

    public SoundUploadedPacket(String title, String artist, String fileName)
    {
        this.title = title;
        this.artist = artist;
        this.fileName = fileName;
    }

    @Override
    public void fromBytes(ByteBuf bytes)
    {
        int titleLength = bytes.readInt();
        char[] titleChars = new char[titleLength];
        for (int i = 0; i < titleLength; i++) titleChars[i] = bytes.readChar();
        title = String.valueOf(titleChars);

        int artistLength = bytes.readInt();
        char[] artistChars = new char[artistLength];
        for (int i = 0; i < artistLength; i++) artistChars[i] = bytes.readChar();
        artist = String.valueOf(artistChars);

        int fileNameLength = bytes.readInt();
        char[] fileNameChars = new char[fileNameLength];
        for (int i = 0; i < fileNameLength; i++) fileNameChars[i] = bytes.readChar();
        fileName = String.valueOf(fileNameChars);

        SoundInfo soundInfo = new SoundInfo(title, artist);

        File soundFile = NetworkHelper.createFileFromByteArr(NetworkHandler.soundUploaded(title), artist, fileName);
        Sound sound = new Sound(soundFile);
        SoundHandler.addLocalSound(soundInfo, sound);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) DelayedPlayHandler.onSoundReceived(soundInfo);
        else
        {
            EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(artist);
            if (player != null) SoundsCool.network.sendTo(new SoundReceivedPacket(soundInfo), player);
        }
    }

    @Override
    public void toBytes(ByteBuf bytes)
    {
        bytes.writeInt(title.length());
        for (char c : title.toCharArray()) bytes.writeChar(c);
        bytes.writeInt(artist.length());
        for (char c : artist.toCharArray()) bytes.writeChar(c);
        bytes.writeInt(fileName.length());
        for (char c : fileName.toCharArray()) bytes.writeChar(c);
    }

    public static class ServerSideHandler implements IMessageHandler<SoundUploadedPacket, IMessage>
    {
        @Override
        public IMessage onMessage(SoundUploadedPacket message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            TargetPoint targetPoint = new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 8);
            SoundsCool.network.sendToAllAround(new SoundReceivedPacket(new SoundInfo(message.title, message.artist)), targetPoint);
            return null;
        }
    }

    public static class ClientSideHandler implements IMessageHandler<SoundUploadedPacket, IMessage>
    {
        @Override
        public IMessage onMessage(SoundUploadedPacket message, MessageContext ctx)
        {
            return null;
        }
    }
}