package com.dynious.soundscool.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

public class NetworkHandler
{
    private static final Map<String, byte[]> soundChunks = new HashMap<>();

    public static void addSoundChunk(String soundName, byte[] soundChunk)
    {
        if (soundChunks.containsKey(soundName)) soundChunks.put(soundName, ArrayUtils.addAll(soundChunks.get(soundName), soundChunk));
        else soundChunks.put(soundName, soundChunk);
    }

    public static byte[] soundUploaded(String soundName)
    {
        return soundChunks.remove(soundName);
    }
}