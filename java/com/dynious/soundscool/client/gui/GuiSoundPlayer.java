package com.dynious.soundscool.client.gui;

import java.awt.Component;
import java.awt.HeadlessException;
import java.util.UUID;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.dynious.soundscool.SoundsCool;
import com.dynious.soundscool.client.audio.SoundPlayer;
import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.helper.NetworkHelper;
import com.dynious.soundscool.helper.SoundHelper;
import com.dynious.soundscool.network.packet.client.GetUploadedSoundsPacket;
import com.dynious.soundscool.network.packet.client.RemoveSoundPacket;
import com.dynious.soundscool.network.packet.client.SoundPlayerPlayPacket;
import com.dynious.soundscool.sound.Sound;
import com.dynious.soundscool.sound.Sound.SoundState;
import com.dynious.soundscool.tileentity.TileSoundPlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.io.FileUtils;

import com.dynious.soundscool.SoundsCool;
import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.helper.NetworkHelper;
import com.dynious.soundscool.helper.SoundHelper;
import com.dynious.soundscool.network.packet.client.GetUploadedSoundsPacket;
import com.dynious.soundscool.network.packet.client.SoundPlayerPlayPacket;
import com.dynious.soundscool.sound.Sound;
import com.dynious.soundscool.tileentity.TileSoundPlayer;

@SideOnly(Side.CLIENT)
public class GuiSoundPlayer extends GuiScreen implements IListGui
{
    private GuiSoundsList soundsList;
    private TileSoundPlayer tile;
    private GuiButton playButton;
    private JFileChooser fileChooser;
    private GuiButton uploadButton;
    private UUID currentlyPlayerSoundId;
    private long timeSoundFinishedPlaying;
    private Sound selectedSound;

    public GuiSoundPlayer(TileSoundPlayer tile)
    {
        this.tile = tile;
        NetworkHelper.syncPlayerSounds(Minecraft.getMinecraft().thePlayer);
        
        selectedSound = tile.getSelectedSound();
        
        try
        {
            fileChooser = new JFileChooser(Minecraft.getMinecraft().mcDataDir)
            {
                @Override
                protected JDialog createDialog(Component parent)
                        throws HeadlessException
                {
                    JDialog dialog = super.createDialog(parent);
                    dialog.setLocationByPlatform(true);
                    dialog.setAlwaysOnTop(true);
                    return dialog;
                }
            };
            fileChooser.setFileFilter(new FileNameExtensionFilter("Sound Files (.ogg, .wav, .mp3)", "ogg", "wav", "mp3"));
        }
        catch (Exception e)
        {
            System.out.println("Exception when creating FileChooser! Are you running an old version of Windows?");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        super.initGui();
        soundsList = new GuiSoundsList(this, 150);
        this.buttonList.add(new GuiButton(0, getWidth() / 2, getHeight() - 32, I18n.format("gui.done")));
        this.buttonList.add(playButton = new GuiButton(1, getWidth() / 2, getHeight() - 57, "Play Sound"));
        
        GuiButton fileButton = new GuiButton(2, 10, getHeight() - 32, 150, 20, "Select File");
        this.buttonList.add(fileButton);
        if (fileChooser == null)
            fileButton.enabled = false;
        
        this.buttonList.add(uploadButton = new GuiButton(3, getWidth() / 2, getHeight() - 82, "Upload"));
        uploadButton.enabled = false;
        
        onSelectedSoundChanged();
    }

    @Override
    public void drawScreen(int p_571_1_, int p_571_2_, float p_571_3_)
    {
    	if(selectedSound != tile.getSelectedSound())
    		onSelectedSoundChanged();
        this.soundsList.drawScreen(p_571_1_, p_571_2_, p_571_3_);
        super.drawScreen(p_571_1_, p_571_2_, p_571_3_);

        if (selectedSound != null)
        {
            this.getFontRenderer().drawString(selectedSound.getSoundName(), getWidth()/2 + 100 - (this.getFontRenderer().getStringWidth(selectedSound.getSoundName())/2), 30, 0xFFFFFF);

            String downloaded = selectedSound.hasLocal()? "Downloaded": "Not downloaded";
            this.getFontRenderer().drawString(downloaded, getWidth()/2 + 100 - (this.getFontRenderer().getStringWidth(downloaded)/2), 60, selectedSound.hasLocal()? 0x00FF00: 0xFF0000);
            
            String uploaded = selectedSound.hasRemote()? "Uploaded": "Not uploaded";
            this.getFontRenderer().drawString(uploaded, getWidth()/2 + 100 - (this.getFontRenderer().getStringWidth(uploaded)/2), 90, selectedSound.hasRemote()? 0x00FF00: 0xFF0000);

            String category = selectedSound.getRemoteCategory();
            this.getFontRenderer().drawString(category, getWidth()/2 + 100 - (this.getFontRenderer().getStringWidth(category)/2), 120, 0xFFFFFF);

            if (selectedSound.getSoundLocation() != null)
            {
                String space = FileUtils.byteCountToDisplaySize(selectedSound.getSoundLocation().length());
                this.getFontRenderer().drawString(space, getWidth()/2 + 100 - (this.getFontRenderer().getStringWidth(space)/2), 150, 0xFFFFFF);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
            switch (button.id)
            {
                case 0:
                    this.mc.displayGuiScreen(null);
                    this.mc.setIngameFocus();
                    break;
                case 1:
                    if (tile.getSelectedSound() != null)
                    {
                    	SoundsCool.network.sendToServer(new SoundPlayerPlayPacket(tile));
                    }
                    else if (selectedSound != null)
                    {
                        if (System.currentTimeMillis() > timeSoundFinishedPlaying)
                        {
                            currentlyPlayerSoundId = UUID.randomUUID();
                            timeSoundFinishedPlaying = (long)(SoundHelper.getSoundLength(selectedSound.getSoundLocation())*1000) + System.currentTimeMillis();
                            SoundPlayer.playSound(selectedSound.getSoundLocation(), currentlyPlayerSoundId.toString(), (float)mc.thePlayer.posX, (float)mc.thePlayer.posY, (float)mc.thePlayer.posZ, false);
                            playButton.displayString = "Stop Sound";
                        }
                        else
                        {
                            timeSoundFinishedPlaying = 0;
                            playButton.displayString = "Play Sound";
                            SoundPlayer.stopSound(currentlyPlayerSoundId.toString());
                        }
                    }
                    break;
                case 2:
                	if (Minecraft.getMinecraft().isFullScreen())
                    {
                        Minecraft.getMinecraft().toggleFullscreen();
                    }
                    int fcReturn = fileChooser.showOpenDialog(null);
                    if (Minecraft.getMinecraft().gameSettings.fullScreen != Minecraft.getMinecraft().isFullScreen())
                    {
                        Minecraft.getMinecraft().toggleFullscreen();
                    }
                    if (fcReturn == JFileChooser.APPROVE_OPTION)
                    {
                        selectSoundIndex(-1);
                        selectedSound = new Sound(fileChooser.getSelectedFile());
                        onSelectedSoundChanged();
                    }
                    break;
                case 3:
                    if (selectedSound != null)
                    {
                    	if(Minecraft.getMinecraft().func_147104_D() != null)
                    	{
                    		if (selectedSound.getState() == Sound.SoundState.LOCAL_ONLY)
                    		{
                    			if(SoundHandler.getLocalSounds().contains(selectedSound))
                    			{
                    				NetworkHelper.clientSoundUpload(selectedSound);
                    			}
                    			else
                    			{
                    				NetworkHelper.clientSoundUpload(SoundHandler.setupSound(selectedSound.getSoundLocation()));
                    			}
                    			
                            	selectSoundIndex(-1);
                    		}
                    		else
                    		{
                    			SoundsCool.network.sendToServer(new RemoveSoundPacket(selectedSound.getSoundName()));
                    			SoundHandler.removeSound(selectedSound);
                    			selectSoundIndex(-1);
                    		}
                    	}
                    	else
                    	{
                    		if(!SoundHandler.getLocalSounds().contains(selectedSound))
                    		{
                    			Sound sound = SoundHandler.setupSound(selectedSound.getSoundLocation());
                    			SoundHandler.addLocalSound(sound.getSoundName(), sound.getSoundLocation());
                    			selectSoundIndex(-1);
                    		}
                    		else
                    		{
                    			selectSoundIndex(-1);
                    			//delete
                    		}
                    	}
                    }
                    break;
            }
    }

    public void onSelectedSoundChanged()
    {
    	selectedSound = tile.getSelectedSound();
    	
    	if(selectedSound != null)
    	{
    		playButton.enabled = true;
    		if(Minecraft.getMinecraft().func_147104_D() != null)
    		{
    			if (selectedSound.hasRemote())
    			{
    				uploadButton.displayString = "Delete";
    				uploadButton.enabled = selectedSound.getRemoteCategory().equals(mc.thePlayer.getDisplayName());
    			}
    			else
    			{
    				uploadButton.displayString = "Upload and Save";
    				uploadButton.enabled = true;
    			}
    		}
    		else
    		{
    			if (selectedSound.getState()==SoundState.SYNCED)
    			{
    				uploadButton.displayString = "Delete";
    			}
    			else
    			{
    				uploadButton.displayString = "Save";
    			}
    			uploadButton.enabled = true;
    		}
    	}
    }

    @Override
    public Minecraft getMinecraftInstance()
    {
        return mc;
    }

    @Override
    public FontRenderer getFontRenderer()
    {
        return fontRendererObj;
    }

    @Override
    public void selectSoundIndex(int selected)
    {
        if (selected >= 0 && selected < SoundHandler.getSounds().size())
        {
            tile.selectSound(SoundHandler.getSounds().get(selected).getSoundName());
            //selectedSound = tile.getSelectedSound();
            onSelectedSoundChanged();
        }
    }

    @Override
    public boolean soundIndexSelected(int var1)
    {
        Sound sound = tile.getSelectedSound();
        return sound != null && SoundHandler.getSounds().indexOf(sound) == var1;
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public void drawBackground()
    {
        drawDefaultBackground();
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
    
    @Override
    public void onGuiClosed()
    {
        if (System.currentTimeMillis() < timeSoundFinishedPlaying)
        {
            SoundPlayer.stopSound(currentlyPlayerSoundId.toString());
        }
    }
}
