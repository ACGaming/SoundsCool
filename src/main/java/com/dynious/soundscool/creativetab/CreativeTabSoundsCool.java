package com.dynious.soundscool.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.dynious.soundscool.block.ModBlocks;

public class CreativeTabSoundsCool extends CreativeTabs
{
    public CreativeTabSoundsCool(String name)
    {
        super(name);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack createIcon()
    {
        return new ItemStack(ModBlocks.soundPlayer);
    }
}