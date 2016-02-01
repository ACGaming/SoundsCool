package com.dynious.soundscool.helper;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.dynious.soundscool.SoundsCool;
import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.lib.Reference;
import com.dynious.soundscool.network.packet.SoundChunkPacket;
import com.dynious.soundscool.network.packet.SoundUploadedPacket;
import com.dynious.soundscool.network.packet.client.GetUploadedSoundsPacket;
import com.dynious.soundscool.network.packet.server.UploadedSoundsPacket;
import com.dynious.soundscool.sound.Sound;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class NetworkHelper
{
    public static final int PARTITION_SIZE = 30000;
    
    public static void syncPlayerSounds(EntityPlayer player)
    {
    	SoundHandler.findSounds();
    	SoundsCool.network.sendToServer(new GetUploadedSoundsPacket(player));
    }
    
    public static void syncAllPlayerSounds()
    {
    	SoundHandler.findSounds();
    	SoundsCool.network.sendToAll(new UploadedSoundsPacket());
    }

    @SideOnly(Side.CLIENT)
    public static void clientSoundUpload(Sound sound)
    {
        sound.setState(Sound.SoundState.UPLOADING);
        uploadSound(sound, sound.getCategory());
    }

    public static void serverSoundUpload(Sound sound, EntityPlayerMP player)
    {
        byte[] soundBytes = convertFileToByteArr(sound.getSoundLocation());
        if(soundBytes != null)
        {
        	for (int i = 0; i < soundBytes.length; i += PARTITION_SIZE)
        	{
        		byte[] bytes = ArrayUtils.subarray(soundBytes, i, i + Math.min(PARTITION_SIZE, soundBytes.length - i));
        		SoundsCool.network.sendTo(new SoundChunkPacket(sound.getSoundName(), bytes), player);
        	}
        	SoundsCool.network.sendTo(new SoundUploadedPacket(sound.getSoundName(), sound.getCategory()), player);
        }
    }

    private static void uploadSound(Sound sound, String category)
    {
        byte[] soundBytes = convertFileToByteArr(sound.getSoundLocation());
        if(soundBytes != null)
        {
        	for (int i = 0; i < soundBytes.length; i += PARTITION_SIZE)
        	{
        		byte[] bytes = ArrayUtils.subarray(soundBytes, i, i + Math.min(PARTITION_SIZE, soundBytes.length - i));
        		SoundsCool.network.sendToServer(new SoundChunkPacket(sound.getSoundName(), bytes));
        	}
        	SoundsCool.network.sendToServer(new SoundUploadedPacket(sound.getSoundName(), category));
        }
    }

    public static byte[] convertFileToByteArr(File file)
    {
        if (file != null && file.exists())
        {
            try
            {
                return FileUtils.readFileToByteArray(file);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static File createFileFromByteArr(byte[] byteFile, String category, String fileName)
    {
        if (byteFile != null && byteFile.length > 0 && !category.isEmpty() && !fileName.isEmpty())
        {
            File file = new File(SoundHandler.getSoundsFolder().getAbsolutePath() + File.separator + Reference.name + File.separator + category + File.separator + fileName);
            try
            {
                FileUtils.writeByteArrayToFile(file, byteFile);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return file;
        }
        return null;
    }
}
