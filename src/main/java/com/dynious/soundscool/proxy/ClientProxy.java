package com.dynious.soundscool.proxy;

import javax.swing.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

import com.dynious.soundscool.handler.ClientConnectionHandler;
import com.dynious.soundscool.handler.TickHandler;
import com.dynious.soundscool.handler.event.SoundEventHandler;
import com.dynious.soundscool.lib.Names;
import com.dynious.soundscool.lib.Reference;

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
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void registerModels()
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getByNameOrId(Reference.modid + ":" + Names.soundPlayer), 0, new ModelResourceLocation(Reference.modid + ":" + Names.soundPlayer, "inventory"));
    }
}