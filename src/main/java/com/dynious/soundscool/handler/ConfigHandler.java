package com.dynious.soundscool.handler;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.dynious.soundscool.lib.Reference;

@Config(modid = Reference.modid, name = Reference.name)
public class ConfigHandler
{
    @Config.Name("Fading Distance")
    @Config.Comment("Sets the fading distance of played sounds")
    public static int fadingDistance = 16;

    @Mod.EventBusSubscriber(modid = Reference.modid)
    public static class EventHandler
    {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equals(Reference.modid)) ConfigManager.sync(Reference.modid, Config.Type.INSTANCE);
        }
    }
}