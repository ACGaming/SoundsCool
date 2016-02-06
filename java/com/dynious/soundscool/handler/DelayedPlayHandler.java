package com.dynious.soundscool.handler;

import java.util.HashMap;
import java.util.Map;

import com.dynious.soundscool.sound.SoundInfo;
import com.dynious.soundscool.sound.SoundPlayInfo;

public class DelayedPlayHandler
{
    private static Map<SoundInfo, SoundPlayInfo> map = new HashMap<SoundInfo, SoundPlayInfo>();

    public static void addDelayedPlay(String soundName, String category, String identifier, int x, int y, int z)
    {
        map.put(new SoundInfo(soundName, category), new SoundPlayInfo(identifier, x, y, z));
    }

    public static void onSoundReceived(String soundName, String category)
    {
        SoundPlayInfo info = map.get(new SoundInfo(soundName, category));
        if (info != null)
        {
            SoundHandler.playSound(soundName, category, info.identifier, info.x, info.y, info.z);
            map.remove(soundName);
        }
    }

    public static void removeSound(String soundName)
    {
        map.remove(soundName);
    }
}
