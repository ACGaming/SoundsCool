package com.dynious.soundscool.handler.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;

import com.dynious.soundscool.SoundsCool;
import com.dynious.soundscool.network.packet.server.StopAllSoundsPacket;

public class PlayerEventHandler {
	
	@SubscribeEvent
	public void onChangeDimension(PlayerChangedDimensionEvent event)
	{
		SoundsCool.network.sendTo(new StopAllSoundsPacket(), (EntityPlayerMP)event.player);
	}

}
