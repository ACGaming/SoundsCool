package com.dynious.soundscool.handler.event;

import net.minecraftforge.client.event.sound.SoundSetupEvent;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecWav;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.cuina.fireandfuel.CodecJLayerMP3;

public class SoundEventHandler
{
    @SubscribeEvent
    public void onSoundLoad(SoundSetupEvent event)
    {
        try
        {
            SoundSystemConfig.setCodec("wav", CodecWav.class);
            SoundSystemConfig.setCodec("mp3", CodecJLayerMP3.class);
        } catch (SoundSystemException e)
        {
            e.printStackTrace();
        }
    }
}
