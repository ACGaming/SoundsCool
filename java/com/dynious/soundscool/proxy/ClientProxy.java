package com.dynious.soundscool.proxy;

import com.dynious.soundscool.handler.ClientConnectionHandler;
import com.dynious.soundscool.handler.TickHandler;
import com.dynious.soundscool.handler.event.SoundEventHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraftforge.common.MinecraftForge;

import javax.swing.*;

public class ClientProxy extends CommonProxy
{
    @Override
    public void initTileEntities()
    {
        super.initTileEntities();
    }

    @Override
    public void soundSetup()
    {
        super.soundSetup();

        MinecraftForge.EVENT_BUS.register(new SoundEventHandler());
        FMLCommonHandler.instance().bus().register(new TickHandler());
        FMLCommonHandler.instance().bus().register(new ClientConnectionHandler());
    }

    @Override
    public void UISetup()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
