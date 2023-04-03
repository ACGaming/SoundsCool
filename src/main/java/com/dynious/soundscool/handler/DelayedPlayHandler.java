package com.dynious.soundscool.handler;

import java.util.HashMap;
import java.util.Map;

import com.dynious.soundscool.sound.SoundInfo;
import com.dynious.soundscool.sound.SoundPlayInfo;

public class DelayedPlayHandler
{
    private static final Map<SoundInfo, SoundPlayInfo> map = new HashMap<>();

    public static void addDelayedPlay(SoundInfo soundInfo, String identifier, int x, int y, int z)
    {
        map.put(soundInfo, new SoundPlayInfo(identifier, x, y, z));
    }

    public static void onSoundReceived(SoundInfo soundInfo)
    {
        SoundPlayInfo info = map.get(soundInfo);
        if (info != null)
        {
            SoundHandler.playSound(soundInfo, info.identifier, info.x, info.y, info.z);
            map.remove(soundInfo);
        }
    }

    public static void removeSound(SoundInfo soundInfo)
    {
        map.remove(soundInfo);
    }
}