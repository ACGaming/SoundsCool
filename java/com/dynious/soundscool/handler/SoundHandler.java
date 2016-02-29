package com.dynious.soundscool.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.dynious.soundscool.SoundsCool;
import com.dynious.soundscool.client.audio.SoundPlayer;
import com.dynious.soundscool.helper.SoundHelper;
import com.dynious.soundscool.helper.TagInfo;
import com.dynious.soundscool.lib.Reference;
import com.dynious.soundscool.network.packet.client.CheckPresencePacket;
import com.dynious.soundscool.sound.Sound;
import com.dynious.soundscool.sound.Sound.SoundState;
import com.dynious.soundscool.sound.SoundInfo;
import com.google.common.io.Files;

public class SoundHandler
{
    private static File soundsFolder;
    private static ConcurrentSkipListMap<SoundInfo, Sound> localSounds = new ConcurrentSkipListMap<SoundInfo, Sound>();
    private static TreeMap<SoundInfo, Sound> remoteSounds = new TreeMap<SoundInfo, Sound>();
    public static int serverListSize = 0;
    public static ArrayList<Sound> guiRemoteList = new ArrayList<Sound>();

    public static File getSoundsFolder()
    {
        if (soundsFolder == null)
        {
            findSounds();
        }
        return soundsFolder;
    }

    public static ConcurrentSkipListMap<SoundInfo, Sound> getLocalSounds()
    {
        return localSounds;
    }

    public static TreeMap<SoundInfo, Sound> getRemoteSounds()
    {
        return remoteSounds;
    }

    public static Sound getSound(SoundInfo soundInfo)
    {
    	Sound sound = localSounds.get(soundInfo);
    	if(sound == null)
    		return remoteSounds.get(soundInfo);
    	return sound;
    }

    public static void findSounds()
    {
        soundsFolder = new File("sounds");
        if (!soundsFolder.exists())
        {
            soundsFolder.mkdir();
        }
        new Thread()
        {
        	public void run() 
        	{
        		addSoundsFromDir(soundsFolder); 
        	};
        }.start();

    }
    
    public static void clientRemoveSound(Sound sound, String identifier)
    {
    	if (sound != null && sound.getSoundLocation() != null)
        {
        	SoundPlayer.getInstance().removeSound(identifier, sound.getSoundLocation().getName());
            sound.getSoundLocation().delete();
            localSounds.remove(sound.getSoundInfo());
        }
    }

    public static void removeSound(Sound sound)
    {
        if (sound != null && sound.getSoundLocation() != null)
        {
            sound.getSoundLocation().delete();
            remoteSounds.remove(sound.getSoundInfo());   		
        }
    }

    private static void addSoundsFromDir(File dir)
    {
        for (File file : dir.listFiles())
        {
            if (file.isFile())
            {
                if (file.getName().endsWith(".ogg") || file.getName().endsWith(".wav") || file.getName().endsWith(".mp3"))
                {
                	Sound sound = new Sound(file);
                	if(!localSounds.containsKey(sound.getSoundInfo()))
                		localSounds.put(sound.getSoundInfo(), sound);
                }
            }
            else if (file.isDirectory())
            {
                addSoundsFromDir(file);
            }
        }
    }

    public static Sound addRemoteSound(SoundInfo soundInfo)
    {
    	Sound sound = new Sound(soundInfo);
    	remoteSounds.put(soundInfo, sound);
    	return sound;
    }

    public static void addLocalSound(SoundInfo soundInfo, Sound sound)
    {
        localSounds.put(soundInfo, sound);
    }

    public static void remoteRemovedSound(SoundInfo soundInfo)
    {
        remoteSounds.remove(soundInfo);
        
        Sound localSound = localSounds.get(soundInfo);
        if(localSound == null)
        	return;
        localSound.setState(SoundState.LOCAL_ONLY);
        localSounds.put(soundInfo, localSound);
    }

    @SideOnly(Side.CLIENT)
    public static void playSound(SoundInfo soundInfo, String identifier, int x, int y, int z)
    {
        Sound sound = localSounds.get(soundInfo);
        if (sound != null)
        {
        	SoundPlayer.getInstance().playSound(sound.getSoundLocation(), identifier, x, y, z, true);
            return;
        }
        else
        {
            sound = addRemoteSound(soundInfo);
        }

        if (sound.getState() != Sound.SoundState.DOWNLOADING)
        {
            sound.setState(Sound.SoundState.DOWNLOADING);
            DelayedPlayHandler.addDelayedPlay(soundInfo, identifier, x, y, z);
            SoundsCool.network.sendToServer(new CheckPresencePacket(soundInfo, true));
        }
    }
    @SideOnly(Side.CLIENT)
    public static Sound setupSound(File file)
    {
    	TagInfo tagInfo = SoundHelper.getTagInfo(file);
        File category = new File("sounds" + File.separator + Reference.name + File.separator + tagInfo.getArtist());
        if (!category.exists())
        {
            category.mkdirs();
        }
        
        File newFile = new File(category.getAbsolutePath() + File.separator + file.getName());
        try
        {
            //TODO: FIXXXX
            if ((!newFile.exists() || !Files.equal(file, newFile)) && !SoundHelper.isSoundInSoundsFolder(file))
            {
                Files.copy(file, newFile);
            }
            else
            {
                return new Sound(file);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return new Sound(newFile);
    }
    
    public static void reset()
    {
    	localSounds = new ConcurrentSkipListMap<SoundInfo, Sound>();
    	remoteSounds = new TreeMap<SoundInfo, Sound>();
    	findSounds();
    }

}
