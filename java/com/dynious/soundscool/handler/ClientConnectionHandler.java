package com.dynious.soundscool.handler;

import java.util.ArrayList;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

import com.dynious.soundscool.client.audio.SoundPlayer;
import com.dynious.soundscool.sound.Sound;

public class ClientConnectionHandler
{
	@SubscribeEvent
	public void disconnect(ClientDisconnectionFromServerEvent event)
	{
		SoundPlayer.getInstance().stopSounds();
		SoundHandler.guiRemoteList = new ArrayList<Sound>();
		SoundHandler.reset();
	}
}
