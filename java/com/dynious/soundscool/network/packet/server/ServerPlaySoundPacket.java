package com.dynious.soundscool.network.packet.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.dynious.soundscool.handler.SoundHandler;

public class ServerPlaySoundPacket implements IMessage
{
    String soundName, category, identifier;
    int x, y, z;
    public ServerPlaySoundPacket()
    {
    }

    public ServerPlaySoundPacket(String soundName, String category, String identifier, int x, int y, int z)
    {
        this.soundName = soundName;
        this.category = category;
        this.identifier = identifier;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf bytes)
    {
    	int soundLength = bytes.readInt();
        char[] fileCars = new char[soundLength];
        for (int i = 0; i < soundLength; i++)
        {
            fileCars[i] = bytes.readChar();
        }
        soundName = String.valueOf(fileCars);
        
        int catLength = bytes.readInt();
        char[] catCars = new char[catLength];
        for (int i = 0; i < catLength; i++)
        {
            catCars[i] = bytes.readChar();
        }
        category = String.valueOf(catCars);
        
        int identLength = bytes.readInt();
        char[] identCars = new char[identLength];
        for (int i = 0; i < identLength; i++)
        {
            identCars[i] = bytes.readChar();
        }
        identifier = String.valueOf(identCars);
        
        x = bytes.readInt();
        y = bytes.readInt();
        z = bytes.readInt();
        SoundHandler.playSound(soundName, category, identifier, x, y, z);
    }

    @Override
    public void toBytes(ByteBuf bytes)
    {
        bytes.writeInt(soundName.length());
        for (char c : soundName.toCharArray())
        {
            bytes.writeChar(c);
        }
        
        bytes.writeInt(category.length());
        for (char c : category.toCharArray())
        {
            bytes.writeChar(c);
        }
        
        bytes.writeInt(identifier.length());
        for (char c : identifier.toCharArray())
        {
            bytes.writeChar(c);
        }
        bytes.writeInt(x);
        bytes.writeInt(y);
        bytes.writeInt(z);
    }
    
    public static class Handler implements IMessageHandler<ServerPlaySoundPacket, IMessage> {
        @Override
        public IMessage onMessage(ServerPlaySoundPacket message, MessageContext ctx) {
            return null;
        }
    }
}
