package com.dynious.soundscool.client.gui;

import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

import com.dynious.soundscool.SoundsCool;
import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.network.packet.client.GetUploadedSoundsPacket;
import com.dynious.soundscool.sound.Sound;
import com.dynious.soundscool.sound.SoundInfo;

public class GuiRemoteSoundsList extends GuiScrollingList
{
    private final IListGui parent;
    private int start = Integer.MAX_VALUE;
    private int finish = -1;
    private int lastStart = 0;
    private long lastTime = System.currentTimeMillis();

    public GuiRemoteSoundsList(IListGui parent, int listWidth)
    {
        super(parent.getMinecraftInstance(), listWidth, parent.getHeight(), 30, parent.getHeight() - 32, 10, 35);
        this.parent = parent;
        SoundsCool.network.sendToServer(new GetUploadedSoundsPacket(0, this.listHeight / 36));
    }

    @Override
    protected int getSize()
    {
        return SoundHandler.serverListSize;
    }

    @Override
    protected void elementClicked(int var1, boolean var2)
    {
        int index = var1 - lastStart;
        this.parent.selectSoundIndex(index);
    }

    @Override
    protected boolean isSelected(int var1)
    {
        int index = var1 - start;
        if (var1 == 0 && start == Integer.MAX_VALUE) index = 0;
        return this.parent.soundIndexSelected(index);
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
        if (listIndex < start)
        {
            start = listIndex;
            if (lastStart < start && !SoundHandler.guiRemoteList.isEmpty()) SoundHandler.guiRemoteList.remove(0);
            else if (lastStart > start && !SoundHandler.guiRemoteList.isEmpty()) SoundHandler.guiRemoteList.add(0, new Sound(new SoundInfo("", "")));
        }
        finish = listIndex;
        int index = listIndex - start;
        if (index >= SoundHandler.guiRemoteList.size()) return;
        Sound sound = SoundHandler.guiRemoteList.get(index);
        if (sound != null)
        {
            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(sound.getSoundName(), listWidth - 10), this.left + 3, var3 + 2, 0xFFFFFF);
            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(sound.getCategory(), listWidth - 10), this.left + 3, var3 + 12, 0xCCCCCC);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        start = Integer.MAX_VALUE;
        finish = -1;
        super.drawScreen(mouseX, mouseY, partialTicks);
        if ((lastStart != start && System.currentTimeMillis() - lastTime > 50) || (System.currentTimeMillis() - lastTime > 1000))
        {
            SoundsCool.network.sendToServer(new GetUploadedSoundsPacket(start, finish + 1));
            lastStart = start;
            lastTime = System.currentTimeMillis();
        }
    }
}