package com.dynious.soundscool.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.dynious.soundscool.SoundsCool;
import com.dynious.soundscool.client.audio.SoundPlayer;
import com.dynious.soundscool.helper.SoundHelper;
import com.dynious.soundscool.helper.TagInfo;
import com.dynious.soundscool.lib.Reference;
import com.dynious.soundscool.network.packet.client.CheckPresencePacket;
import com.dynious.soundscool.sound.Sound;
import com.google.common.io.Files;

public class SoundHandler
{
    private static File soundsFolder;
    private static ArrayList<Sound> sounds = new ArrayList<Sound>();
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
    public static ArrayList<Sound> getSounds()
    {
        if (sounds == null)
        {
            findSounds();
        }
        return sounds;
    }

    public static ArrayList<Sound> getLocalSounds()
    {
        ArrayList<Sound> localSounds = new ArrayList<Sound>();
        for (Sound sound : getSounds())
        {
            if (sound.hasLocal())
                localSounds.add(sound);
        }
        return localSounds;
    }

    public static ArrayList<Sound> getRemoteSounds()
    {
        ArrayList<Sound> remoteSounds = new ArrayList<Sound>();
        for (Sound sound : getSounds())
        {
            if (sound.hasRemote())
                remoteSounds.add(sound);
        }
        return remoteSounds;
    }

    public static Sound getSound(String title, String category)
    {
    	Iterator<Sound> iter = sounds.iterator();

    	while (iter.hasNext()) {
    	    Sound sound = iter.next();

    	    if(sound.getSoundName().equals(title) && sound.getCategory().equals(category))
            {
                return sound;
            }
    	}
        return null;
    }

    public static void findSounds()
    {
        soundsFolder = new File("sounds");
        if (!soundsFolder.exists())
        {
            soundsFolder.mkdir();
        }
        addSoundsFromDir(soundsFolder);
    }
    
    public static void clientRemoveSound(Sound sound, String identifier)
    {
    	SoundPlayer.getInstance().removeSound(identifier, sound.getSoundLocation().getName());
    	removeSound(sound);
    }

    public static void removeSound(Sound sound)
    {
        if (sound != null && sound.getSoundLocation() != null)
        {
            sound.getSoundLocation().delete();
            sounds.remove(sound);
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
                	if(!sounds.contains(sound))
                		sounds.add(sound);
                }
            }
            else if (file.isDirectory())
            {
                addSoundsFromDir(file);
            }
        }
        Collections.sort(sounds);
    }

    public static void addRemoteSound(String soundName, String remoteCategory)
    {
        Sound sound = getSound(soundName, remoteCategory);
        if (sound != null)
        {
            if (sound.hasLocal())
            {
                sound.onSoundUploaded(remoteCategory);
            }
        }
        else
        {
            sounds.add(new Sound(soundName, remoteCategory));
            Collections.sort(sounds);
        }
    }

    public static void addLocalSound(String soundName, String category, File soundFile)
    {
        Sound sound = getSound(soundName, category);
        if (sound != null)
        {
            if (sound.getState() != Sound.SoundState.SYNCED)
            {
                sound.onSoundDownloaded(soundFile);
            }
        }
        else
        {
            sounds.add(new Sound(soundFile));
        }
        Collections.sort(sounds);
    }

    public static void remoteRemovedSound(Sound sound)
    {
        if (!sound.hasLocal())
        {
            sounds.remove(sound);
        }
        else
        {
            sound.setState(Sound.SoundState.LOCAL_ONLY);
            sounds.remove(sound);
            sounds.add(sound);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void playSound(String soundName, String category, String identifier, int x, int y, int z)
    {
        Sound sound = SoundHandler.getSound(soundName, category);
        if (sound != null)
        {
            if (sound.hasLocal())
            {
                SoundPlayer.getInstance().playSound(sound.getSoundLocation(), identifier, x, y, z, true);
                return;
            }
        }
        else
        {
            SoundHandler.addRemoteSound(soundName, category);
            sound = SoundHandler.getSound(soundName, category);
        }

        if (sound.getState() != Sound.SoundState.DOWNLOADING)
        {
            sound.setState(Sound.SoundState.DOWNLOADING);
            DelayedPlayHandler.addDelayedPlay(soundName, category, identifier, x, y, z);
            SoundsCool.network.sendToServer(new CheckPresencePacket(soundName, category, true));
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
        
        File newFile = new File(category.getAbsolutePath() + File.separator + tagInfo.getTitle());
        try
        {
            //TODO: FIXXXX
            if ((!newFile.exists() || !Files.equal(file, newFile)) && !SoundHelper.isSoundInSoundsFolder(file))
            {
                Files.copy(file, newFile);
                findSounds();
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
    	sounds = new ArrayList<Sound>();
    	findSounds();
    }

}
