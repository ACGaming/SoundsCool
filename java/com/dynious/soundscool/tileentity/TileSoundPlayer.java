package com.dynious.soundscool.tileentity;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import com.dynious.soundscool.SoundsCool;
import com.dynious.soundscool.client.audio.SoundPlayer;
import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.helper.SoundHelper;
import com.dynious.soundscool.network.packet.client.SoundPlayerSelectPacket;
import com.dynious.soundscool.network.packet.server.ServerPlaySoundPacket;
import com.dynious.soundscool.network.packet.server.SoundRemovedPacket;
import com.dynious.soundscool.network.packet.server.StopSoundPacket;
import com.dynious.soundscool.sound.Sound;
import com.dynious.soundscool.sound.SoundInfo;

public class TileSoundPlayer extends TileEntity implements ITickable
{
    private boolean isPowered = false;
    private Sound selectedSound;
    private String lastSoundIdentifier = "";
    private long timeSoundFinishedPlaying;
    private int count = 0;

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

    public void selectSound(SoundInfo soundInfo)
    {
        this.selectedSound = SoundHandler.getSound(soundInfo);

        if (worldObj.isRemote)
        {
        	SoundsCool.network.sendToServer(new SoundPlayerSelectPacket(this));
        }
        else
        {
        	sync();
        }
    }

    public Sound getSelectedSound()
    {
    	if(selectedSound != null)
    		return SoundHandler.getSound(new SoundInfo(selectedSound.getSoundName(), selectedSound.getCategory()));
    	else
    		return null;
    }

    public void playCurrentSound()
    {
        if (selectedSound != null)
        {
            if (!isPlaying())
            {
                if (SoundHandler.getLocalSounds().containsKey(selectedSound.getSoundInfo()))
                {
                    lastSoundIdentifier = UUID.randomUUID().toString();
                    timeSoundFinishedPlaying = (long)(SoundHelper.getSoundLength(selectedSound.getSoundLocation())*1000) + System.currentTimeMillis();
                    TargetPoint tp = new TargetPoint(getWorld().provider.getDimensionId(), pos.getX(), pos.getY(), pos.getZ(), 64);
                    SoundsCool.network.sendToAllAround(new ServerPlaySoundPacket(selectedSound.getSoundInfo(), lastSoundIdentifier, pos.getX(), pos.getY(), pos.getZ()), tp);
                }
                else
                {
                	selectedSound = null;
                }
                sync();
            }
            else
            {
                stopCurrentSound();
                sync();
            }
        }
    }

    public void stopCurrentSound()
    {
        if (selectedSound != null && isPlaying())
        {
        	TargetPoint targetPoint = new TargetPoint(worldObj.provider.getDimensionId(), pos.getX(), pos.getY(), pos.getZ(), 64);
            SoundsCool.network.sendToAllAround(new StopSoundPacket(lastSoundIdentifier), targetPoint);
            timeSoundFinishedPlaying = 0;
        }
    }
    
    public boolean isPlaying()
    {
    	return System.currentTimeMillis() < timeSoundFinishedPlaying;
    }
    
    public String getIdentifier()
    {
    	return lastSoundIdentifier;
    }
    
    private void reset()
    {
    	stopCurrentSound();
    	selectedSound = null;
    	lastSoundIdentifier = "";
    	timeSoundFinishedPlaying = 0;
    	sync();
    }
    
    @Override
    public void update()
    {	
    	if(worldObj.isRemote)
		{
			if(timeSoundFinishedPlaying == 0 && SoundPlayer.getInstance().isPlaying(lastSoundIdentifier))
			{
				SoundPlayer.getInstance().stopSound(lastSoundIdentifier);
			}
		}
    	else if(count == 0)
    	{
    		if(selectedSound != null && !SoundHandler.getLocalSounds().containsKey(selectedSound.getSoundInfo()))
    		{
    			TargetPoint targetPoint = new TargetPoint(worldObj.provider.getDimensionId(), pos.getX(), pos.getY(), pos.getZ(), 8);
                SoundsCool.network.sendToAllAround(new SoundRemovedPacket(selectedSound.getSoundName(), selectedSound.getCategory()), targetPoint);
    			reset();
    		}
    	}
    	count = ++count % 50;
    }
    
    private void sync()
    {
    	worldObj.markBlockForUpdate(pos);
    	markDirty();
    }
    
    @Override
    public void invalidate()
    {
    	super.invalidate();
    	if(worldObj.isRemote)
    	{
    		SoundPlayer.getInstance().stopSound(lastSoundIdentifier);
    	}
    }
    
    @Override
    public void onChunkUnload()
    {
    	if(worldObj.isRemote && isPlaying())
    	{
    		SoundPlayer.getInstance().stopSound(lastSoundIdentifier);
    	}
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        selectedSound = SoundHandler.getSound(new SoundInfo(compound.getString("name"), compound.getString("category")));
        lastSoundIdentifier = compound.getString("lastSoundIdentifier");
        timeSoundFinishedPlaying = compound.getLong("timeSoundFinishedPlaying");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        if (selectedSound != null)
        {
            compound.setString("name", selectedSound.getSoundName());
            compound.setString("category", selectedSound.getCategory());
            compound.setString("lastSoundIdentifier", lastSoundIdentifier);
            compound.setLong("timeSoundFinishedPlaying", timeSoundFinishedPlaying);
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        String soundName = pkt.getNbtCompound().getString("name");
        String category = pkt.getNbtCompound().getString("category");
        SoundInfo soundInfo = new SoundInfo(soundName, category);
        
        if((selectedSound != null && !selectedSound.getSoundInfo().equals(soundInfo)) || selectedSound == null)
        {
        	selectedSound = SoundHandler.getLocalSounds().get(soundInfo);
        	if(soundName.length() != 0 && category.length() != 0)
        	{
        		if(selectedSound == null)
        		{
        			selectedSound = new Sound(soundInfo);
        		}
        		SoundHandler.addRemoteSound(soundInfo);
        	}
        }
        
        this.timeSoundFinishedPlaying = pkt.getNbtCompound().getLong("timeSoundFinishedPlaying");
        this.lastSoundIdentifier = pkt.getNbtCompound().getString("lastSoundIdentifier");
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound compound = new NBTTagCompound();
        if (selectedSound != null)
        {
            compound.setString("name", selectedSound.getSoundName());
            compound.setString("category", selectedSound.getCategory());
            compound.setLong("timeSoundFinishedPlaying", timeSoundFinishedPlaying);
            compound.setString("lastSoundIdentifier", lastSoundIdentifier);
        }
        return new S35PacketUpdateTileEntity(pos, 1, compound);
    }
}
