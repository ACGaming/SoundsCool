package com.dynious.soundscool.client.gui;

import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

import com.dynious.soundscool.sound.Sound;

public class GuiLocalSoundsList extends GuiScrollingList
{
    private final IListGui parent;

    public GuiLocalSoundsList(IListGui parent, int listWidth)
    {
        super(parent.getMinecraftInstance(), listWidth, parent.getWidth(), 30, parent.getHeight() - 32, 10, 35);
        this.parent = parent;
    }

    @Override
    protected int getSize()
    {
        return parent.getSounds().size();
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
    protected int getContentHeight()
    {
        return (this.getSize()) * 35 + 1;
    }

    @Override
    protected void drawBackground()
    {
        this.parent.drawBackground();
    }

    @Override
    protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5)
    {
        Sound sound = parent.getSounds().get(listIndex);
        if (sound != null)
        {
            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(sound.getSoundName(), listWidth - 10), this.left + 3, var3 + 2, 0xFFFFFF);
            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(sound.getCategory(), listWidth - 10), this.left + 3, var3 + 12, 0xCCCCCC);
        }
    }
}