package com.dynious.soundscool.network.packet.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.dynious.soundscool.tileentity.TileSoundPlayer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SoundPlayerPlayPacket implements IMessage
{
    int dimensionId;
    int x, y, z;
    public SoundPlayerPlayPacket()
    {
    }

    public SoundPlayerPlayPacket(TileSoundPlayer tile)
    {
        this.dimensionId = tile.getWorld().provider.dimensionId;
        this.x = tile.xCoord;
        this.y = tile.yCoord;
        this.z = tile.zCoord;
    }

    @Override
    public void fromBytes(ByteBuf bytes)
    {
        dimensionId = bytes.readInt();
        x = bytes.readInt();
        y = bytes.readInt();
        z = bytes.readInt();
        World world = DimensionManager.getWorld(dimensionId);
        if (world != null)
        {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile != null && tile instanceof TileSoundPlayer)
            {
                ((TileSoundPlayer)tile).playCurrentSound();
                world.markBlockForUpdate(x, y, z);
                tile.markDirty();
            }
        }
    }

    @Override
    public void toBytes(ByteBuf bytes)
    {
        bytes.writeInt(dimensionId);
        bytes.writeInt(x);
        bytes.writeInt(y);
        bytes.writeInt(z);
    }
    
    public static class Handler implements IMessageHandler<SoundPlayerPlayPacket, IMessage> {
        @Override
        public IMessage onMessage(SoundPlayerPlayPacket message, MessageContext ctx) {
            return null;
        }
    }
}
