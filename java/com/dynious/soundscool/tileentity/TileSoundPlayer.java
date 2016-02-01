package com.dynious.soundscool.tileentity;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import com.dynious.soundscool.SoundsCool;
import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.helper.NetworkHelper;
import com.dynious.soundscool.helper.SoundHelper;
import com.dynious.soundscool.network.packet.client.SoundPlayerSelectPacket;
import com.dynious.soundscool.network.packet.server.ServerPlaySoundPacket;
import com.dynious.soundscool.network.packet.server.StopSoundPacket;
import com.dynious.soundscool.sound.Sound;

public class TileSoundPlayer extends TileEntity
{
    private boolean isPowered = false;
    private Sound selectedSound;
    private String lastSoundIdentifier;
    private long timeSoundFinishedPlaying;

    public void setPowered(boolean powered)
    {
        if (!isPowered && powered)
        {
            playCurrentSound();
            isPowered = true;
        }
        else if (isPowered && !powered)
        {
            isPowered = false;
        }
    }

    public void selectSound(String soundName, String category)
    {
        this.selectedSound = SoundHandler.getSound(soundName, category);

        if (this.getWorld().isRemote)
        {
        	SoundsCool.network.sendToServer(new SoundPlayerSelectPacket(this));
        }
    }

    public Sound getSelectedSound()
    {
        return selectedSound;
    }

    public void playCurrentSound()
    {
    	NetworkHelper.syncAllPlayerSounds();
        if (selectedSound != null)
        {
            if (!isPlaying())
            {
                if (SoundHandler.getSound(selectedSound.getSoundName(), selectedSound.getCategory()) != null)
                {
                    lastSoundIdentifier = UUID.randomUUID().toString();
                    timeSoundFinishedPlaying = (long)(SoundHelper.getSoundLength(selectedSound.getSoundLocation())*1000) + System.currentTimeMillis();
                    //TODO Change to sendMessageToAllAround
                    NetworkHelper.sendMessageToAll(new ServerPlaySoundPacket(selectedSound.getSoundName(), selectedSound.getCategory(), lastSoundIdentifier, pos.getX(), pos.getY(), pos.getZ()));
                }
                else
                {
                selectedSound = null;
                }
            }
            else
            {
                stopCurrentSound();
            }
        }
    }

    public void stopCurrentSound()
    {
        if (selectedSound != null && isPlaying())
        {
            NetworkHelper.sendMessageToAll(new StopSoundPacket(lastSoundIdentifier));
            timeSoundFinishedPlaying = 0;
        }
    }
    
    public boolean isPlaying()
    {
    	return System.currentTimeMillis() < timeSoundFinishedPlaying;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        selectedSound = SoundHandler.getSound(compound.getString("name"), compound.getString("category"));
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        if (selectedSound != null)
        {
            compound.setString("name", selectedSound.getSoundName());
            compound.setString("category", selectedSound.getCategory());
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
		String soundName = pkt.getNbtCompound().getString("selectedSound");
        String category = pkt.getNbtCompound().getString("category");
		this.selectedSound = SoundHandler.getSound(soundName, category);
        Long timeSoundFinishedPlaying = pkt.getNbtCompound().getLong("timeSoundFinishedPlaying");
        this.timeSoundFinishedPlaying = timeSoundFinishedPlaying;
    }

    @Override
    public Packet getDescriptionPacket()
    {
    	NetworkHelper.syncAllPlayerSounds();
        NBTTagCompound compound = new NBTTagCompound();
        if (selectedSound != null)
        {
            compound.setString("name", selectedSound.getSoundName());
            compound.setString("category", selectedSound.getCategory());
            compound.setLong("timeSoundFinishedPlaying", timeSoundFinishedPlaying);
        }
        return new S35PacketUpdateTileEntity(pos, 1, compound);
    }
}
