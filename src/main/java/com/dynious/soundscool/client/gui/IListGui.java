package com.dynious.soundscool.client.gui;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import com.dynious.soundscool.sound.Sound;

public interface IListGui
{
    Minecraft getMinecraftInstance();

    FontRenderer getFontRenderer();

    void selectSoundIndex(int selected);

    boolean soundIndexSelected(int var1);

    int getWidth();

    int getHeight();

    void drawBackground();

    ArrayList<Sound> getSounds();
}