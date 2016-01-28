package com.dynious.soundscool.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.dynious.soundscool.client.gui.GuiSoundPlayer;
import com.dynious.soundscool.tileentity.TileSoundPlayer;

import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch(ID)
        {
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch(ID)
        {
            case 1:
                TileEntity tile = world.getTileEntity(x, y, z);
                if (tile != null && tile instanceof TileSoundPlayer)
                {
                    return new GuiSoundPlayer((TileSoundPlayer)tile);
                }
            default:
                return null;
        }
    }
}
