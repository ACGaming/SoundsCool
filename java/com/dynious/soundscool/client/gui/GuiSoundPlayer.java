package com.dynious.soundscool.client.gui;

import java.awt.Component;
import java.awt.HeadlessException;
import java.util.UUID;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.io.FileUtils;

import com.dynious.soundscool.SoundsCool;
import com.dynious.soundscool.client.audio.SoundPlayer;
import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.helper.NetworkHelper;
import com.dynious.soundscool.helper.SoundHelper;
import com.dynious.soundscool.network.packet.client.RemoveSoundPacket;
import com.dynious.soundscool.network.packet.client.SoundPlayerPlayPacket;
import com.dynious.soundscool.sound.Sound;
import com.dynious.soundscool.sound.Sound.SoundState;
import com.dynious.soundscool.tileentity.TileSoundPlayer;

@SideOnly(Side.CLIENT)
public class GuiSoundPlayer extends GuiScreen implements IListGui
{
    private GuiSoundsList soundsListGui;
    private TileSoundPlayer tile;
    private GuiButton playButton;
    private JFileChooser fileChooser;
    private GuiButton uploadButton;
    private String currentSoundID;
    private long timeSoundFinishedPlaying;
    private Sound selectedSound;

    public GuiSoundPlayer(TileSoundPlayer tile)
    {
        this.tile = tile;
        
        selectedSound = tile.getSelectedSound(); 
        NetworkHelper.syncPlayerSounds(Minecraft.getMinecraft().thePlayer);  
         
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
        soundsListGui = new GuiSoundsList(this, 150);
        
        this.buttonList.add(new GuiButton(0, getWidth() / 2, getHeight() - 32, I18n.format("gui.done")));
        this.buttonList.add(playButton = new GuiButton(1, getWidth() / 2, getHeight() - 57, "Play Sound"));
        
        GuiButton fileButton = new GuiButton(2, 10, getHeight() - 32, 150, 20, "Select File");
        this.buttonList.add(fileButton);
        if (fileChooser == null)
            fileButton.enabled = false;
        
        this.buttonList.add(uploadButton = new GuiButton(3, getWidth() / 2, getHeight() - 82, "Upload"));
    }

    @Override
    public void drawScreen(int p_571_1_, int p_571_2_, float p_571_3_)
    {
        soundsListGui.drawScreen(p_571_1_, p_571_2_, p_571_3_);
        super.drawScreen(p_571_1_, p_571_2_, p_571_3_);

        if (selectedSound != null)
        {
        	Sound tileSound = tile.getSelectedSound();
        	if((!selectedSound.equals(tileSound) && selectedSound.hasRemote()) || (selectedSound.equals(tileSound) && selectedSound.getState()!=tileSound.getState() && tileSound.hasRemote()))
        	{
        		if(tileSound != null)
        		{
        			selectedSound = tileSound;
        		}
        	}
        	
        	String name = selectedSound.getSoundName();
            this.getFontRenderer().drawString(name, getWidth()/2 + 100 - (this.getFontRenderer().getStringWidth(name)/2), 30, 0xFFFFFF);

            SoundState downloaded = selectedSound.getState();
            this.getFontRenderer().drawString(downloaded.name(), getWidth()/2 + 100 - (this.getFontRenderer().getStringWidth(downloaded.name())/2), 60, 0x00FF00);

            String category = selectedSound.getCategory();
            this.getFontRenderer().drawString(category, getWidth()/2 + 100 - (this.getFontRenderer().getStringWidth(category)/2), 90, 0xFFFFFF);

            if (selectedSound.getSoundLocation() != null)
            {
                String space = FileUtils.byteCountToDisplaySize(selectedSound.getSoundLocation().length());
                this.getFontRenderer().drawString(space, getWidth()/2 + 100 - (this.getFontRenderer().getStringWidth(space)/2), 120, 0xFFFFFF);
            }
        }
        updateButtons();
        
        if(tile.isInvalid())
    		this.mc.displayGuiScreen(null);
    }
    
    private void updateButtons()
    {
    	if(selectedSound != null)
    	{	
    		if(!Minecraft.getMinecraft().isIntegratedServerRunning())
    		{
    			if (selectedSound.hasRemote())
    			{
    				uploadButton.displayString = "Delete";
    				uploadButton.enabled = selectedSound.getCategory().equals(mc.thePlayer.getDisplayName());
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
    		
    		if(tile.isPlaying() || System.currentTimeMillis() < timeSoundFinishedPlaying)
            {
            	playButton.displayString="Stop Sound";
            }
            else if(selectedSound.hasRemote())
            {
            	playButton.displayString="Play Sound";
            }
            else
            {
            	playButton.displayString="Preview Sound";
            }
    		
    		if(selectedSound.getState().equals(SoundState.DOWNLOADING) || selectedSound.getState().equals(SoundState.UPLOADING))
            {
    			playButton.enabled = false;
    			uploadButton.enabled = false;
            }
    		else
    		{
    			playButton.enabled = true;
    		}
    	}
    	else
    	{
    		playButton.enabled = false;
    		uploadButton.enabled = false;
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
                	if(playButton.displayString.equals("Stop Sound"))
                		stopSound();
                	else
                		playSound();
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
                    	stopSound();
                        selectSoundIndex(-1);
                        selectedSound = new Sound(fileChooser.getSelectedFile());
                        if(!SoundHandler.getSounds().contains(selectedSound))
                        {
                        	selectedSound.setCategory(mc.thePlayer.getDisplayName());
                        	if(SoundHandler.getSounds().contains(selectedSound))
                        	{
                        		selectSoundIndex(SoundHandler.getSounds().indexOf(selectedSound));
                        		return;
                        	}
                        }
                        else
                        {
                        	selectSoundIndex(SoundHandler.getSounds().indexOf(selectedSound));
                        	return;
                        }
                    }
                    break;
                case 3:
                    if (selectedSound != null)
                    {
                    	if(!Minecraft.getMinecraft().isIntegratedServerRunning())
                    	{
                    		if (selectedSound.getState() == Sound.SoundState.LOCAL_ONLY)
                    		{
                    			if(SoundHandler.getLocalSounds().contains(selectedSound))
                    			{
                    				NetworkHelper.clientSoundUpload(selectedSound);
                    			}
                    			else
                    			{
                    				selectedSound = SoundHandler.setupSound(selectedSound.getSoundLocation());
                    				NetworkHelper.clientSoundUpload(selectedSound);
                    			}
                    			tile.selectSound(selectedSound.getSoundName(), selectedSound.getCategory());
                    			stopSound();
                    		}
                    		else
                    		{
                    			SoundsCool.network.sendToServer(new RemoveSoundPacket(selectedSound.getSoundName(), selectedSound.getCategory()));
                    			SoundHandler.removeSound(selectedSound);
                    			selectSoundIndex(-1);
                    			stopSound();
                    		}
                    	}
                    	else
                    	{
                    		if(!SoundHandler.getLocalSounds().contains(selectedSound))
                    		{
                    			selectedSound = SoundHandler.setupSound(selectedSound.getSoundLocation());
                    			SoundHandler.addLocalSound(selectedSound.getSoundName(), selectedSound.getCategory(), selectedSound.getSoundLocation());

                    			tile.selectSound(selectedSound.getSoundName(), selectedSound.getCategory());
                            	stopSound();
                    		}
                    		else
                    		{
                    			SoundHandler.removeSound(selectedSound);
                    			selectSoundIndex(-1);
                    			stopSound();
                    		}
                    	}
                    }
                    break;
            }
    }
    
    private void playSound()
    {
    	if(selectedSound.equals(tile.getSelectedSound()))
    	{
    		SoundsCool.network.sendToServer(new SoundPlayerPlayPacket(tile));	
    	}
    	else if(selectedSound != null)
    	{
    		currentSoundID = UUID.randomUUID().toString();
    		timeSoundFinishedPlaying = (long)(SoundHelper.getSoundLength(selectedSound.getSoundLocation())*1000) + System.currentTimeMillis();
    		SoundPlayer.getInstance().playSound(selectedSound.getSoundLocation(), currentSoundID, (float)mc.thePlayer.posX, (float)mc.thePlayer.posY, (float)mc.thePlayer.posZ, false);
    	}
    	playButton.displayString = "Stop Sound";
    }
    
    private void stopSound()
    {
    	if(tile.isPlaying())
    	{
    		SoundsCool.network.sendToServer(new SoundPlayerPlayPacket(tile));
    	}
    	if(System.currentTimeMillis() < timeSoundFinishedPlaying)
    	{
    		timeSoundFinishedPlaying = 0;
    		SoundPlayer.getInstance().stopSound(currentSoundID);
    	}
    	playButton.displayString = "Play Sound";
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
    	stopSound();
        if (selected >= 0 && selected < SoundHandler.getSounds().size())
        {
        	selectedSound = SoundHandler.getSounds().get(selected);
        	if(selectedSound.hasRemote() || mc.isIntegratedServerRunning())
        		tile.selectSound(selectedSound.getSoundName(), selectedSound.getCategory());
        	
        	playSound();
        }
        else
        	selectedSound = null;
    }

    @Override
    public boolean soundIndexSelected(int var1)
    {
        return SoundHandler.getSounds().indexOf(selectedSound) == var1;
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
    	SoundPlayer.getInstance().stopSound(currentSoundID);
    }
}
