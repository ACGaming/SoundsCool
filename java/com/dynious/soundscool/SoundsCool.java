package com.dynious.soundscool;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import com.dynious.soundscool.block.ModBlocks;
import com.dynious.soundscool.creativetab.CreativeTabSoundsCool;
import com.dynious.soundscool.handler.GuiHandler;
import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.lib.Reference;
import com.dynious.soundscool.proxy.CommonProxy;

@Mod(modid = Reference.modid, name = Reference.name, version = Reference.version)
public class SoundsCool
{
    @Instance(Reference.modid)
    public static SoundsCool instance;

    @SidedProxy(clientSide = Reference.clientProxy, serverSide = Reference.commonProxy)
    public static CommonProxy proxy;

    public static CreativeTabs tabSoundsCool = new CreativeTabSoundsCool(CreativeTabs.getNextID(), Reference.modid);
    
    public static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel("soundscool");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	proxy.registerMessages();

    	proxy.soundSetup();
    	
    	Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
    	Logger.getLogger("org.jaudiotagger.audio").setLevel(Level.OFF);
    	Logger.getLogger("org.jaudiotagger.tag.id3").setLevel(Level.OFF);

        proxy.UISetup();

        SoundHandler.findSounds();

        ModBlocks.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.registerBlocks();

        proxy.initTileEntities();

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }
}
