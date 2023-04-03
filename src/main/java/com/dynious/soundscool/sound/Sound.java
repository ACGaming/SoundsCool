package com.dynious.soundscool.sound;

import java.io.File;

import com.dynious.soundscool.helper.SoundHelper;
import com.dynious.soundscool.helper.TagInfo;

public class Sound
{
    private final String soundName;
    private File soundLocation;
    private String category;
    private SoundState state;

    public Sound(File soundLocation)
    {
        this.soundLocation = soundLocation;
        TagInfo tagInfo = SoundHelper.getTagInfo(soundLocation);
        this.soundName = tagInfo.getTitle();
        this.category = tagInfo.getArtist();
        this.state = SoundState.LOCAL_ONLY;
    }

    public Sound(SoundInfo soundInfo)
    {
        this.soundLocation = null;
        this.soundName = soundInfo.name;
        this.category = soundInfo.category;
        this.state = SoundState.REMOTE_ONLY;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getSoundName()
    {
        return soundName;
    }

    public File getSoundLocation()
    {
        return soundLocation;
    }

    public SoundState getState()
    {
        return state;
    }

    public void setState(SoundState state)
    {
        this.state = state;
    }

    public SoundInfo getSoundInfo()
    {
        return new SoundInfo(soundName, category);
    }

    public void onSoundUploaded(String remoteCategory)
    {
        this.state = SoundState.SYNCED;
    }

    public void onSoundDownloaded(File soundFile)
    {
        this.soundLocation = soundFile;
        this.state = SoundState.SYNCED;
    }

    public boolean hasRemote()
    {
        return this.state == SoundState.SYNCED || this.state == SoundState.REMOTE_ONLY || this.state == SoundState.DOWNLOADING;
    }

    public boolean hasLocal()
    {
        return this.state == SoundState.SYNCED || this.state == SoundState.LOCAL_ONLY || this.state == SoundState.UPLOADING;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((soundName == null) ? 0 : soundName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Sound other = (Sound) obj;
        if (category == null)
        {
            if (other.category != null) return false;
        }
        else if (!category.equals(other.category)) return false;
        if (soundName == null)
        {
            return other.soundName == null;
        }
        else return soundName.equals(other.soundName);
    }

    public enum SoundState
    {
        LOCAL_ONLY, REMOTE_ONLY, SYNCED, DOWNLOADING, UPLOADING
    }
}