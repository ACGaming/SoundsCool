package com.dynious.soundscool.network.packet.client;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.dynious.soundscool.handler.SoundHandler;
import com.dynious.soundscool.sound.Sound;
import com.dynious.soundscool.sound.SoundInfo;
import io.netty.buffer.ByteBuf;

public class RemoveSoundPacket implements IMessage
{
    String soundName, category;

    public RemoveSoundPacket() {}

    public RemoveSoundPacket(SoundInfo soundInfo)
    {
        this.soundName = soundInfo.name;
        this.category = soundInfo.category;
    }

    @Override
    public void fromBytes(ByteBuf bytes)
    {
        int fileLength = bytes.readInt();
        char[] fileCars = new char[fileLength];
        for (int i = 0; i < fileLength; i++) fileCars[i] = bytes.readChar();
        soundName = String.valueOf(fileCars);

        int catLength = bytes.readInt();
        char[] catCars = new char[catLength];
        for (int i = 0; i < catLength; i++) catCars[i] = bytes.readChar();
        category = String.valueOf(catCars);

        Sound sound = SoundHandler.getSound(new SoundInfo(soundName, category));
        SoundHandler.removeSound(sound);

    }

    @Override
    public void toBytes(ByteBuf bytes)
    {
        bytes.writeInt(soundName.length());
        for (char c : soundName.toCharArray()) bytes.writeChar(c);

        bytes.writeInt(category.length());
        for (char c : category.toCharArray()) bytes.writeChar(c);
    }

    public static class Handler implements IMessageHandler<RemoveSoundPacket, IMessage>
    {
        @Override
        public IMessage onMessage(RemoveSoundPacket message, MessageContext ctx)
        {
            return null;
        }
    }
}