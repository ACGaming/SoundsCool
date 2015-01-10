package com.dynious.soundscool.network.packet.server;

import io.netty.buffer.ByteBuf;

import com.dynious.soundscool.helper.GuiHelper;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class OpenGUIPacket implements IMessage
{
    int ID = -1;
    public OpenGUIPacket()
    {
    }

    public OpenGUIPacket(int guiID)
    {
        this.ID = guiID;
    }

    @Override
    public void fromBytes(ByteBuf bytes)
    {
        this.ID = bytes.readInt();
        GuiHelper.openGui(ID);
    }

    @Override
    public void toBytes(ByteBuf bytes)
    {
        bytes.writeInt(ID);
    }
    
    public static class Handler implements IMessageHandler<OpenGUIPacket, IMessage> {
        @Override
        public IMessage onMessage(OpenGUIPacket message, MessageContext ctx) {
            return null;
        }
    }
}
