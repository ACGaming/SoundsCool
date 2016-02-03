package com.dynious.soundscool.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.dynious.soundscool.block.ModBlocks;

public class CreativeTabSoundsCool extends CreativeTabs
{
    public CreativeTabSoundsCool(int id, String name)
    {
        super(id, name);
    }

    @Override
    public Item getTabIconItem()
    {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public ItemStack getIconItemStack()
    {
        return new ItemStack(ModBlocks.soundPlayer);
    }
}
