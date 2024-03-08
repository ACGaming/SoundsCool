package com.dynious.soundscool.client.audio;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.dynious.soundscool.handler.ConfigHandler;
import paulscode.sound.SoundSystem;

@SideOnly(Side.CLIENT)
public class SoundPlayer
{
    private static final SoundPlayer instance = new SoundPlayer();

    private static SoundSystem soundSystem;

    public static SoundPlayer getInstance()
    {
        init();
        return instance;
    }

    private static void init()
    {
        if (soundSystem == null || soundSystem.randomNumberGenerator == null)
        {
            SoundManager soundManager = ObfuscationReflectionHelper.getPrivateValue(net.minecraft.client.audio.SoundHandler.class, Minecraft.getMinecraft().getSoundHandler(), "sndManager", "field_147694_f", "V");
            soundSystem = ObfuscationReflectionHelper.getPrivateValue(SoundManager.class, soundManager, "sndSystem", "field_148620_e", "e");
        }
    }

    private ArrayList<String> playing = new ArrayList<>();

    public void playSound(File sound, String identifier, float x, float y, float z, boolean fading)
    {
        try
        {
            soundSystem.newStreamingSource(false, identifier, sound.toURI().toURL(), sound.getName(), false, x, y, z, fading ? 2 : 0, ConfigHandler.fadingDistance);
            soundSystem.play(identifier);
            playing.add(identifier);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    public void removeSound(String identifier, String fileName)
    {
        stopSound(identifier);
        soundSystem.removeSource(identifier);
        soundSystem.unloadSound(fileName);
    }

    public void stopSound(String identifier)
    {
        if (isPlaying(identifier))
        {
            soundSystem.stop(identifier);
            playing.remove(identifier);
        }
    }

    public void stopSounds()
    {
        for (String s : playing)
        {
            soundSystem.stop(s);
            soundSystem.removeSource(s);
        }
        playing = new ArrayList<>();
    }

    public void pauseSounds()
    {
        for (String s : playing) soundSystem.pause(s);
    }

    public void resumeSounds()
    {
        for (String s : playing) soundSystem.play(s);
    }

    public boolean isPlaying(String identifier)
    {
        return soundSystem.playing(identifier);
    }
}