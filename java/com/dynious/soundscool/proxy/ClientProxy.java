package com.dynious.soundscool.proxy;

import javax.swing.UIManager;

import net.minecraftforge.common.MinecraftForge;

import com.dynious.soundscool.handler.ClientConnectionHandler;
import com.dynious.soundscool.handler.TickHandler;
import com.dynious.soundscool.handler.event.SoundEventHandler;

import cpw.mods.fml.common.FMLCommonHandler;

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
