package com.dynious.soundscool.helper;

import java.io.File;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class SoundHelper
{
    public static double getSoundLength(File soundFile)
    {
        try
        {
            AudioHeader audioHeader = AudioFileIO.read(soundFile).getAudioHeader();
            if (soundFile.getName().endsWith(".mp3"))
            {
                return ((MP3AudioHeader)audioHeader).getPreciseTrackLength();
            }
            else
            {
                return ((GenericAudioHeader)audioHeader).getPreciseLength();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean isSoundInSoundsFolder(File soundFile)
    {
        String path = soundFile.getAbsolutePath();

        if (path.endsWith("sounds") || path.endsWith("sounds" + File.separator))
        {
            return true;
        }
        path = path.substring(0, path.lastIndexOf(File.separator));
        path = path.substring(path.lastIndexOf(File.separator) + 1);
        return path.equals("sounds");
    }
    
    public static TagInfo getTagInfo(File soundFile)
    {
    	TagInfo tagInfo = new TagInfo();
    	Tag tag;
    	try 
		{
    		tag = AudioFileIO.read(soundFile).getTag();
    		if(tag == null)
    			throw new NullPointerException();
		}
    	catch (Exception e)
    	{
    		tagInfo.setTitle(soundFile.getName());
    		tagInfo.setArtist("Unknown Artist");
    		return tagInfo;
    	}
    	
    	String title = tag.getFirst(FieldKey.TITLE);
		if(title.length() != 0)
			tagInfo.setTitle(title);
		else
			tagInfo.setTitle(soundFile.getName());
		
		String artist = tag.getFirst(FieldKey.ARTIST).replaceAll("[^a-zA-Z0-9.-]", " ").trim();
		if(artist.length() != 0)
			tagInfo.setArtist(artist);
		else
			tagInfo.setArtist("Unknown Artist");
		
    	return tagInfo;
    }
}
