package com.dynious.soundscool.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

public class GuiAdminPanel extends GuiScreen
{
    private final EntityPlayer player;
    private GuiTextField maxSounds;

    public GuiAdminPanel(EntityPlayer player)
    {
        this.player = player;
    }

    @Override
    public void drawScreen(int p_571_1_, int p_571_2_, float p_571_3_) {}

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id == 0)
            {
                this.mc.displayGuiScreen(null);
                this.mc.setIngameFocus();
            }
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.add(new GuiButton(0, getWidth() / 2, getHeight() - 42, I18n.format("gui.done")));
        maxSounds = new GuiTextField(0, fontRenderer, getWidth() / 2, getHeight() / 2, 20, 15);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }
}
