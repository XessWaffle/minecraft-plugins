package com.xessmcserver.serverworldutilities.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCurrent implements CommandExecutor {

    public CommandCurrent(){

    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(s.equals("current")){
            Player sender = Bukkit.getPlayer(commandSender.getName());
            commandSender.sendMessage(sender.getWorld().getName());
            return true;
        }

        return false;
    }
}
