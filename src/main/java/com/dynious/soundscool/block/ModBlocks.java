package com.dynious.soundscool.block;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.dynious.soundscool.lib.Names;
import com.dynious.soundscool.lib.Reference;

@Mod.EventBusSubscriber
public class ModBlocks
{
    @GameRegistry.ObjectHolder(Reference.namespace + Names.soundPlayer)
    public static BlockSoundPlayer soundPlayer;
}