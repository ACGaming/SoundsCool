package com.dynious.soundscool.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;

import com.dynious.soundscool.helper.NetworkHelper;
import com.dynious.soundscool.lib.Commands;
import com.dynious.soundscool.network.packet.server.OpenGUIPacket;

public class CommandSoundsCool extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return Commands.SOUNDS;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender commandSender)
    {
        return true;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List addTabCompletionOptions(ICommandSender commandSender, String[] args, BlockPos pos)
    {
        return null;
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args)
    {
        if (commandSender instanceof EntityPlayer)
        {
            NetworkHelper.sendMessageToPlayer(new OpenGUIPacket(0), (EntityPlayerMP) commandSender);
        }
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender)
    {
        return null;
    }
}
