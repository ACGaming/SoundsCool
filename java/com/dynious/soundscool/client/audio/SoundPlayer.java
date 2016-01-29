package com.dynious.soundscool.client.audio;

import com.dynious.soundscool.handler.SoundHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import paulscode.sound.SoundSystem;

@SideOnly(Side.CLIENT)
public class SoundPlayer
{
	private static SoundPlayer instance = new SoundPlayer();
	
    private static SoundSystem soundSystem;
    private ArrayList<String> playing = new ArrayList<String>();

    private static void init()
	{
		if(soundSystem == null || soundSystem.randomNumberGenerator == null)
		{
			SoundManager soundManager = ObfuscationReflectionHelper.getPrivateValue(net.minecraft.client.audio.SoundHandler.class, Minecraft.getMinecraft().getSoundHandler(), "sndManager",
					"field_147694_f", "V");
			soundSystem = ObfuscationReflectionHelper.getPrivateValue(SoundManager.class, soundManager, "sndSystem", "field_148620_e", "e");
		}
	}

    public void playSound(File sound, String identifier, float x, float y, float z, boolean fading)
    {
        try
        {
            soundSystem.newStreamingSource(false, identifier, sound.toURI().toURL(), sound.getName(), false, x, y, z, fading ? 2 : 0, 16);
            soundSystem.play(identifier);
            playing.add(identifier);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    public void stopSound(String identifier)
    {
    	if(soundSystem.playing(identifier))
    	{
    		soundSystem.stop(identifier);
    		playing.remove(identifier);
    	}
    }
    
    public void stopSounds()
    {
    	if(soundSystem!=null)
    	{
    		for(String s : playing)
			{
				soundSystem.stop(s);
			}
    	}
    	playing = new ArrayList<String>();
    }
    
    public void pauseSounds()
	{
		if(soundSystem != null)
		{
			for(String s : playing)
			{
				soundSystem.pause(s);
			}
		}
	}
    
    public void resumeSounds()
	{
		if(soundSystem != null)
		{
			for(String s : playing)
			{
				soundSystem.play(s);
			}
		}
	}
    
    public static SoundPlayer getInstance()
	{
    	init();
		return instance;
	}
}
