package com.dynious.soundscool.tileentity;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
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
        else if (isPowered && !powered) isPowered = false;
    }

    public void selectSound(SoundInfo soundInfo)
    {
        this.selectedSound = SoundHandler.getSound(soundInfo);
        if (world.isRemote) SoundsCool.network.sendToServer(new SoundPlayerSelectPacket(this));
        else sync();
    }

    public Sound getSelectedSound()
    {
        if (selectedSound != null) return SoundHandler.getSound(new SoundInfo(selectedSound.getSoundName(), selectedSound.getCategory()));
        else return null;
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
                    timeSoundFinishedPlaying = (long) (SoundHelper.getSoundLength(selectedSound.getSoundLocation()) * 1000) + System.currentTimeMillis();
                    TargetPoint tp = new TargetPoint(getWorld().provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64);
                    SoundsCool.network.sendToAllAround(new ServerPlaySoundPacket(selectedSound.getSoundInfo(), lastSoundIdentifier, pos.getX(), pos.getY(), pos.getZ()), tp);
                }
                else selectedSound = null;
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
            TargetPoint targetPoint = new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64);
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

    @Override
    public void update()
    {
        if (world.isRemote)
        {
            if (timeSoundFinishedPlaying == 0 && SoundPlayer.getInstance().isPlaying(lastSoundIdentifier)) SoundPlayer.getInstance().stopSound(lastSoundIdentifier);
        }
        else if (count == 0)
        {
            if (selectedSound != null && !SoundHandler.getLocalSounds().containsKey(selectedSound.getSoundInfo()))
            {
                TargetPoint targetPoint = new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 8);
                SoundsCool.network.sendToAllAround(new SoundRemovedPacket(selectedSound.getSoundName(), selectedSound.getCategory()), targetPoint);
                reset();
            }
        }
        count = ++count % 50;
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
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        if (selectedSound != null)
        {
            compound.setString("name", selectedSound.getSoundName());
            compound.setString("category", selectedSound.getCategory());
            compound.setString("lastSoundIdentifier", lastSoundIdentifier);
            compound.setLong("timeSoundFinishedPlaying", timeSoundFinishedPlaying);
        }
        return compound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        NBTTagCompound compound = new NBTTagCompound();
        if (selectedSound != null)
        {
            compound.setString("name", selectedSound.getSoundName());
            compound.setString("category", selectedSound.getCategory());
            compound.setLong("timeSoundFinishedPlaying", timeSoundFinishedPlaying);
            compound.setString("lastSoundIdentifier", lastSoundIdentifier);
        }
        return new SPacketUpdateTileEntity(pos, 1, compound);
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        if (world.isRemote) SoundPlayer.getInstance().stopSound(lastSoundIdentifier);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        String soundName = pkt.getNbtCompound().getString("name");
        String category = pkt.getNbtCompound().getString("category");
        SoundInfo soundInfo = new SoundInfo(soundName, category);

        if (selectedSound == null || !selectedSound.getSoundInfo().equals(soundInfo))
        {
            selectedSound = SoundHandler.getLocalSounds().get(soundInfo);
            if (soundName.length() != 0 && category.length() != 0)
            {
                if (selectedSound == null) selectedSound = new Sound(soundInfo);
                SoundHandler.addRemoteSound(soundInfo);
            }
        }

        this.timeSoundFinishedPlaying = pkt.getNbtCompound().getLong("timeSoundFinishedPlaying");
        this.lastSoundIdentifier = pkt.getNbtCompound().getString("lastSoundIdentifier");
    }

    @Override
    public void onChunkUnload()
    {
        if (world.isRemote && isPlaying()) SoundPlayer.getInstance().stopSound(lastSoundIdentifier);
    }

    private void reset()
    {
        stopCurrentSound();
        selectedSound = null;
        lastSoundIdentifier = "";
        timeSoundFinishedPlaying = 0;
        sync();
    }

    private void sync()
    {
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        markDirty();
    }
}