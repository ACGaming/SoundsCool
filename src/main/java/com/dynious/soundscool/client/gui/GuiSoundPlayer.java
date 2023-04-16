package com.dynious.soundscool.client.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.dynious.soundscool.SoundsCool;
import com.dynious.soundscool.client.audio.SoundPlayer;
import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.helper.NetworkHelper;
import com.dynious.soundscool.helper.SoundHelper;
import com.dynious.soundscool.network.packet.client.CheckPresencePacket;
import com.dynious.soundscool.network.packet.client.RemoveSoundPacket;
import com.dynious.soundscool.network.packet.client.SoundPlayerPlayPacket;
import com.dynious.soundscool.sound.Sound;
import com.dynious.soundscool.sound.Sound.SoundState;
import com.dynious.soundscool.tileentity.TileSoundPlayer;

@SideOnly(Side.CLIENT)
public class GuiSoundPlayer extends GuiScreen implements IListGui
{
    private final TileSoundPlayer tile;
    public Sound selectedSound;
    private GuiScrollingList soundsListGui;
    private GuiButton playButton, uploadButton, listButton;
    private JFileChooser fileChooser;
    private String currentSoundID;
    private long timeSoundFinishedPlaying;
    private boolean pause = false;
    private ArrayList<Sound> sounds;

    public GuiSoundPlayer(TileSoundPlayer tile)
    {
        this.tile = tile;
        selectedSound = tile.getSelectedSound();
        SoundHandler.findSounds();
        try
        {
            fileChooser = new JFileChooser(Minecraft.getMinecraft().gameDir)
            {
                @Override
                protected JDialog createDialog(Component parent) throws HeadlessException
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

    @Override
    public void drawScreen(int p_571_1_, int p_571_2_, float p_571_3_)
    {
        soundsListGui.drawScreen(p_571_1_, p_571_2_, p_571_3_);
        super.drawScreen(p_571_1_, p_571_2_, p_571_3_);

        if (selectedSound != null)
        {
            Sound tileSound = tile.getSelectedSound();
            if (tileSound != null && selectedSound.hasRemote()) selectedSound = tileSound;
            if (selectedSound.hasLocal() && SoundHandler.getRemoteSounds().containsKey(selectedSound.getSoundInfo())) selectedSound.setState(SoundState.SYNCED);

            String name = selectedSound.getSoundName();
            this.getFontRenderer().drawString(name, getWidth() / 2 + 100 - (this.getFontRenderer().getStringWidth(name) / 2), 30, 0xFFFFFF);

            SoundState downloaded = selectedSound.getState();
            this.getFontRenderer().drawString(downloaded.name(), getWidth() / 2 + 100 - (this.getFontRenderer().getStringWidth(downloaded.name()) / 2), 60, 0x00FF00);

            String category = selectedSound.getCategory();
            this.getFontRenderer().drawString(category, getWidth() / 2 + 100 - (this.getFontRenderer().getStringWidth(category) / 2), 90, 0xFFFFFF);

            if (selectedSound.getSoundLocation() != null)
            {
                String space = FileUtils.byteCountToDisplaySize(selectedSound.getSoundLocation().length());
                this.getFontRenderer().drawString(space, getWidth() / 2 + 100 - (this.getFontRenderer().getStringWidth(space) / 2), 120, 0xFFFFFF);
            }
        }
        else selectedSound = tile.getSelectedSound();

        updateButtons();

        IntegratedServer server = mc.getIntegratedServer();
        if (pause && (mc.isGamePaused() || server == null || server.getPublic())) selectingFromSystem();

        if (tile.isInvalid()) this.mc.displayGuiScreen(null);

        if (listButton.displayString.equals("Local Sounds")) sounds = new ArrayList<>(SoundHandler.getLocalSounds().values());
        else sounds = SoundHandler.guiRemoteList;
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled) switch (button.id)
        {
            case 0:
                this.mc.displayGuiScreen(null);
                this.mc.setIngameFocus();
                break;
            case 1:
                if (playButton.displayString.equals("Stop Sound")) stopSound();
                else if (selectedSound.getState() == SoundState.LOCAL_ONLY) playSound();
                else if (selectedSound.equals(tile.getSelectedSound())) SoundsCool.network.sendToServer(new SoundPlayerPlayPacket(tile));
                break;
            case 2:
                pause = true;
                break;
            case 3:
                if (selectedSound != null)
                {
                    if (!Minecraft.getMinecraft().isIntegratedServerRunning())
                    {
                        if (selectedSound.getState() == Sound.SoundState.LOCAL_ONLY)
                        {
                            if (SoundHandler.getLocalSounds().containsKey(selectedSound.getSoundInfo())) NetworkHelper.uploadSound(selectedSound);
                            else
                            {
                                selectedSound = SoundHandler.setupSound(selectedSound.getSoundLocation());
                                NetworkHelper.uploadSound(selectedSound);
                            }
                            selectedSound.setState(SoundState.UPLOADING);
                            SoundHandler.addLocalSound(selectedSound.getSoundInfo(), selectedSound);
                            tile.selectSound(selectedSound.getSoundInfo());
                            stopSound();
                            return;
                        }
                    }
                    else if (!SoundHandler.getLocalSounds().containsKey(selectedSound.getSoundInfo()))
                    {
                        selectedSound = SoundHandler.setupSound(selectedSound.getSoundLocation());
                        selectedSound.setState(SoundState.SYNCED);
                        SoundHandler.addLocalSound(selectedSound.getSoundInfo(), selectedSound);
                        tile.selectSound(selectedSound.getSoundInfo());
                        stopSound();
                        return;
                    }
                    SoundsCool.network.sendToServer(new RemoveSoundPacket(selectedSound.getSoundInfo()));
                    selectedSound.setState(SoundState.LOCAL_ONLY);
                    SoundHandler.clientRemoveSound(selectedSound, tile.getIdentifier());
                    selectSoundIndex(-1);
                }
                break;
            case 4:
                if (listButton.displayString.equals("Local Sounds"))
                {
                    listButton.displayString = "Remote Sounds";
                    soundsListGui = new GuiRemoteSoundsList(this, getWidth() / 3);
                }
                else
                {
                    listButton.displayString = "Local Sounds";
                    soundsListGui = new GuiLocalSoundsList(this, getWidth() / 3);
                }
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.buttonList.add(new GuiButton(0, getWidth() / 2, getHeight() - 32, I18n.format("gui.done")));
        this.buttonList.add(playButton = new GuiButton(1, getWidth() / 2, getHeight() - 57, "Play Sound"));
        playButton.enabled = false;

        GuiButton fileButton = new GuiButton(2, 10, getHeight() - 32, getWidth() / 3, 20, "Select File");
        this.buttonList.add(fileButton);
        if (fileChooser == null) fileButton.enabled = false;

        this.buttonList.add(uploadButton = new GuiButton(3, getWidth() / 2, getHeight() - 82, "Upload"));
        uploadButton.enabled = false;

        this.buttonList.add(listButton = new GuiButton(4, 10, 10, getWidth() / 3, 20, "Local Sounds"));

        if (sounds == null)
        {
            soundsListGui = new GuiLocalSoundsList(this, getWidth() / 3);
            sounds = new ArrayList<>(SoundHandler.getLocalSounds().values());
        }
        else if (soundsListGui.getClass().equals(GuiLocalSoundsList.class)) soundsListGui = new GuiLocalSoundsList(this, getWidth() / 3);
        else
        {
            sounds = SoundHandler.guiRemoteList;
            soundsListGui = new GuiRemoteSoundsList(this, getWidth() / 3);
            listButton.displayString = "Remote Sounds";
        }
    }

    @Override
    public void onGuiClosed()
    {
        SoundPlayer.getInstance().stopSound(currentSoundID);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return pause;
    }

    @Override
    public Minecraft getMinecraftInstance()
    {
        return mc;
    }

    @Override
    public FontRenderer getFontRenderer()
    {
        return fontRenderer;
    }

    @Override
    public void selectSoundIndex(int selected)
    {
        stopSound();
        if (selected >= 0 && selected < sounds.size())
        {
            selectedSound = sounds.get(selected);
            tile.selectSound(selectedSound.getSoundInfo());
            SoundsCool.network.sendToServer(new CheckPresencePacket(selectedSound.getSoundInfo(), false));
        }
        else selectedSound = null;
    }

    @Override
    public boolean soundIndexSelected(int var1)
    {
        return sounds.indexOf(selectedSound) == var1;
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
    public ArrayList<Sound> getSounds()
    {
        return sounds;
    }

    private void updateButtons()
    {
        if (selectedSound != null)
        {
            if (!mc.isIntegratedServerRunning())
            {
                if (selectedSound.hasRemote())
                {
                    uploadButton.displayString = "Delete";
                    //uploadButton.enabled = selectedSound.getCategory().equals(mc.player.getDisplayName().getUnformattedText());
                    uploadButton.enabled = false;
                }
                else
                {
                    uploadButton.displayString = "Upload and Save";
                    uploadButton.enabled = true;
                }
            }
            else
            {
                if (selectedSound.getState() == SoundState.SYNCED)
                {
                    uploadButton.displayString = "Delete";
                    uploadButton.enabled = false;
                }
                else
                {
                    uploadButton.displayString = "Save";
                    uploadButton.enabled = true;
                }
            }

            if (tile.isPlaying() || System.currentTimeMillis() < timeSoundFinishedPlaying) playButton.displayString = "Stop Sound";
            else if (selectedSound.hasRemote()) playButton.displayString = "Play Sound";
            else playButton.displayString = "Preview Sound";

            if (selectedSound.getState().equals(SoundState.DOWNLOADING) || selectedSound.getState().equals(SoundState.UPLOADING))
            {
                playButton.enabled = false;
                uploadButton.enabled = false;
            }
            else playButton.enabled = true;
        }
        else
        {
            playButton.enabled = false;
            uploadButton.enabled = false;
        }
    }

    private void selectingFromSystem()
    {
        pause = false;
        if (Minecraft.getMinecraft().isFullScreen()) Minecraft.getMinecraft().toggleFullscreen();
        int fcReturn = fileChooser.showOpenDialog(null);
        if (Minecraft.getMinecraft().gameSettings.fullScreen != Minecraft.getMinecraft().isFullScreen()) Minecraft.getMinecraft().toggleFullscreen();
        if (fcReturn == JFileChooser.APPROVE_OPTION)
        {
            stopSound();
            selectedSound = new Sound(fileChooser.getSelectedFile());
            if (SoundHandler.getRemoteSounds().containsKey(selectedSound.getSoundInfo())) tile.selectSound(selectedSound.getSoundInfo());
        }
    }

    private void playSound()
    {
        if (selectedSound != null)
        {
            currentSoundID = UUID.randomUUID().toString();
            timeSoundFinishedPlaying = (long) (SoundHelper.getSoundLength(selectedSound.getSoundLocation()) * 1000) + System.currentTimeMillis();
            SoundPlayer.getInstance().playSound(selectedSound.getSoundLocation(), currentSoundID, (float) mc.player.posX, (float) mc.player.posY, (float) mc.player.posZ, false);
        }
        playButton.displayString = "Stop Sound";
    }

    private void stopSound()
    {
        if (tile.isPlaying()) SoundsCool.network.sendToServer(new SoundPlayerPlayPacket(tile));
        if (System.currentTimeMillis() < timeSoundFinishedPlaying)
        {
            timeSoundFinishedPlaying = 0;
            SoundPlayer.getInstance().stopSound(currentSoundID);
        }
        playButton.displayString = "Play Sound";
    }
}