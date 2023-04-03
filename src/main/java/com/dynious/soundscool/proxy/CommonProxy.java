package com.dynious.soundscool.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.dynious.soundscool.SoundsCool;
import com.dynious.soundscool.block.BlockSoundPlayer;
import com.dynious.soundscool.block.ModBlocks;
import com.dynious.soundscool.handler.event.PlayerEventHandler;
import com.dynious.soundscool.lib.Names;
import com.dynious.soundscool.lib.Reference;
import com.dynious.soundscool.network.packet.SoundChunkPacket;
import com.dynious.soundscool.network.packet.SoundUploadedPacket;
import com.dynious.soundscool.network.packet.client.*;
import com.dynious.soundscool.network.packet.server.*;
import com.dynious.soundscool.tileentity.TileSoundPlayer;

@Mod.EventBusSubscriber
public class CommonProxy
{
    @SubscribeEvent
    public static void registerBlocksEvent(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(new BlockSoundPlayer());
    }

    @SubscribeEvent
    public static void registerItemsEvent(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemBlock(ModBlocks.soundPlayer).setRegistryName(Names.soundPlayer));
    }

    public void initTileEntities()
    {
        GameRegistry.registerTileEntity(TileSoundPlayer.class, new ResourceLocation(Reference.modid, Names.soundPlayer));
    }

    public void registerMessages()
    {
        SoundsCool.network.registerMessage(CheckPresencePacket.Handler.class, CheckPresencePacket.class, 0, Side.SERVER);
        SoundsCool.network.registerMessage(GetUploadedSoundsPacket.Handler.class, GetUploadedSoundsPacket.class, 1, Side.SERVER);
        SoundsCool.network.registerMessage(RemoveSoundPacket.Handler.class, RemoveSoundPacket.class, 2, Side.SERVER);
        SoundsCool.network.registerMessage(SoundPlayerPlayPacket.Handler.class, SoundPlayerPlayPacket.class, 3, Side.SERVER);
        SoundsCool.network.registerMessage(SoundPlayerSelectPacket.Handler.class, SoundPlayerSelectPacket.class, 4, Side.SERVER);
        SoundsCool.network.registerMessage(OpenGUIPacket.Handler.class, OpenGUIPacket.class, 5, Side.CLIENT);
        SoundsCool.network.registerMessage(ServerPlaySoundPacket.Handler.class, ServerPlaySoundPacket.class, 6, Side.CLIENT);
        SoundsCool.network.registerMessage(SoundNotFoundPacket.Handler.class, SoundNotFoundPacket.class, 7, Side.CLIENT);
        SoundsCool.network.registerMessage(SoundReceivedPacket.Handler.class, SoundReceivedPacket.class, 8, Side.CLIENT);
        SoundsCool.network.registerMessage(SoundRemovedPacket.Handler.class, SoundRemovedPacket.class, 9, Side.CLIENT);
        SoundsCool.network.registerMessage(StopSoundPacket.Handler.class, StopSoundPacket.class, 10, Side.CLIENT);
        SoundsCool.network.registerMessage(UploadedSoundsPacket.Handler.class, UploadedSoundsPacket.class, 11, Side.CLIENT);
        SoundsCool.network.registerMessage(StopAllSoundsPacket.Handler.class, StopAllSoundsPacket.class, 14, Side.CLIENT);
        SoundsCool.network.registerMessage(SoundChunkPacket.Handler.class, SoundChunkPacket.class, 12, Side.CLIENT);
        SoundsCool.network.registerMessage(SoundChunkPacket.Handler.class, SoundChunkPacket.class, 12, Side.SERVER);
        SoundsCool.network.registerMessage(SoundUploadedPacket.ClientSideHandler.class, SoundUploadedPacket.class, 13, Side.CLIENT);
        SoundsCool.network.registerMessage(SoundUploadedPacket.ServerSideHandler.class, SoundUploadedPacket.class, 13, Side.SERVER);
    }

    public void soundSetup()
    {
        FMLCommonHandler.instance().bus().register(new PlayerEventHandler());
    }

    public void UISetup() {}

    public void registerModels() {}
}