package com.dynious.soundscool.client.gui;

import java.util.ArrayList;

import net.minecraft.client.renderer.Tessellator;

import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.sound.Sound;

import cpw.mods.fml.client.GuiScrollingList;

public class GuiSoundsList extends GuiScrollingList
{
    private IListGui parent;

    public GuiSoundsList(IListGui parent, int listWidth)
    {
        super(parent.getMinecraftInstance(), listWidth, parent.getWidth(), 0, parent.getHeight() - 32, 10, 35);
        this.parent = parent;
    }

    @Override
    protected int getSize()
    {
        return SoundHandler.getSounds().size();
    }

    @Override
    protected void elementClicked(int var1, boolean var2)
    {
        this.parent.selectSoundIndex(var1);
    }

    @Override
    protected boolean isSelected(int var1)
    {
        return this.parent.soundIndexSelected(var1);
    }

    @Override
    protected void drawBackground()
    {
        this.parent.drawBackground();
    }

    @Override
    protected int getContentHeight()
    {
        return (this.getSize()) * 35 + 1;
    }

    @Override
    protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5)
    {
    	ArrayList<Sound> sounds = SoundHandler.getSounds();
    	
    	if(sounds.size() <= listIndex)
    		return;
        Sound sound = SoundHandler.getSounds().get(listIndex);
        if (sound != null)
        {
            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(sound.getSoundName(), listWidth - 10), this.left + 3 , var3 + 2, 0xFFFFFF);
            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(sound.getCategory(), listWidth - 10), this.left + 3 , var3 + 12, 0xCCCCCC);
        }
    }
}
