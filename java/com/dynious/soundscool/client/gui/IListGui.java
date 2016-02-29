package com.dynious.soundscool.client.gui;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import com.dynious.soundscool.sound.Sound;

public interface IListGui
{
    public Minecraft getMinecraftInstance();

    public FontRenderer getFontRenderer();

    public void selectSoundIndex(int selected);

    public boolean soundIndexSelected(int var1);

    public int getWidth();

    public int getHeight();

    public void drawBackground();
    
    public ArrayList<Sound> getSounds();
}
