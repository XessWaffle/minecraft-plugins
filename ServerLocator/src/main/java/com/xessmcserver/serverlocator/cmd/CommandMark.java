package com.xessmcserver.serverlocator.cmd;

import com.xessmcserver.serverlocator.ServerLocator;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMark implements CommandExecutor {

    private static ServerLocator ref;
    public CommandMark(ServerLocator ref){
        this.ref = ref;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player locationMarker = Bukkit.getPlayer(commandSender.getName());



        return false;
    }
}
