package com.dynious.soundscool.tileentity;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import com.dynious.soundscool.SoundsCool;
import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.helper.NetworkHelper;
import com.dynious.soundscool.helper.SoundHelper;
import com.dynious.soundscool.network.packet.client.SoundPlayerSelectPacket;
import com.dynious.soundscool.network.packet.server.ServerPlaySoundPacket;
import com.dynious.soundscool.network.packet.server.StopSoundPacket;
import com.dynious.soundscool.sound.Sound;

public class TileSoundPlayer extends TileEntity implements ITickable
{
    private boolean isPowered = false;
    private Sound selectedSound;
    private String lastSoundIdentifier = "";
    private long timeSoundFinishedPlaying;
    private int lastSize;

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
            	String name = selectedSound.getSoundName();
            	String category = selectedSound.getCategory();
                if (SoundHandler.getSound(name, category) != null)
                {
                    lastSoundIdentifier = UUID.randomUUID().toString();
                    timeSoundFinishedPlaying = (long)(SoundHelper.getSoundLength(selectedSound.getSoundLocation())*1000) + System.currentTimeMillis();
                    TargetPoint tp = new TargetPoint(getWorld().provider.getDimensionId(), pos.getX(), pos.getY(), pos.getZ(), 64);
                    SoundsCool.network.sendToAllAround(new ServerPlaySoundPacket(name, category, lastSoundIdentifier, pos.getX(), pos.getY(), pos.getZ()), tp);
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
            SoundsCool.network.sendToAll(new StopSoundPacket(lastSoundIdentifier));
            timeSoundFinishedPlaying = 0;
        }
    }
    
    public boolean isPlaying()
    {
    	return System.currentTimeMillis() < timeSoundFinishedPlaying;
    }
    
    private void reset()
    {
    	stopCurrentSound();
    	selectedSound = null;
    	lastSoundIdentifier = "";
    	timeSoundFinishedPlaying = 0;
    	worldObj.markBlockForUpdate(pos);
    	markDirty();
    }
    
    @Override
    public void update()
    {	
    	if(FMLCommonHandler.instance().getEffectiveSide().isServer())
    	{
    		if(SoundHandler.getSounds().size() < lastSize)
    		{
    			if(selectedSound != null && SoundHandler.getSound(selectedSound.getSoundName(), selectedSound.getCategory())==null)
    			{
    				reset();
    			}
    		}
    		lastSize = SoundHandler.getSounds().size();
    	}
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
        String soundName = pkt.getNbtCompound().getString("name");
        String category = pkt.getNbtCompound().getString("category");
        this.selectedSound = SoundHandler.getSound(soundName, category);
        this.timeSoundFinishedPlaying = pkt.getNbtCompound().getLong("timeSoundFinishedPlaying");
        
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
