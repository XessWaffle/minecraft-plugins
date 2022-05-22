package com.xessmcserver.serverworldutilities.cmd;

import com.xessmcserver.serverworldutilities.io.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.xessmcserver.serverworldutilities.ServerWorldUtilities;
import com.xessmcserver.serverworldutilities.util.WorldList;


public class CommandSwitch implements CommandExecutor {

    private WorldList worlds;

    public ServerWorldUtilities ref;
    public PlayerData dataStore;

    public CommandSwitch(WorldList worlds){
        this.worlds = worlds;
    }

    private void listWorlds(CommandSender commandSender){
        for(World w: worlds.getWorlds()){
            commandSender.sendMessage(w.getName());
        }
    }

    private void handleSwitch(CommandSender commandSender, String[] strings) throws Exception{
        World teleport = worlds.getWorld(strings[0]);

        if(teleport == null){
            throw new Exception("World " + strings[0] + " does not exist");
        }
        Player sender = Bukkit.getServer().getPlayer(commandSender.getName());
        World curr = sender.getWorld();
        if(!curr.getName().equals(strings[0])) {

            dataStore.setLocation(sender.getUniqueId().toString(), curr.getName(), sender.getLocation());
            dataStore.setCurrentWorld(sender, teleport);
            sender.teleport(dataStore.getLocation(sender.getUniqueId().toString(), teleport));

        } else {
            throw new Exception("Can't switch to same world");
        }

    }

    private void handleOpSwitch(String[] strings) throws Exception{
        World teleport = worlds.getWorld(strings[1]);
        if(teleport == null){
            throw new Exception("World " + strings[1] + " does not exist");
        }

        Player sender = Bukkit.getServer().getPlayer(strings[0]);
        if(sender == null){
            throw new Exception("Player " + sender.getName() + " does not exist");
        }

        World curr = sender.getWorld();
        if(!curr.getName().equals(strings[1])) {

            dataStore.setLocation(sender.getUniqueId().toString(), curr.getName(), sender.getLocation());
            dataStore.setCurrentWorld(sender, teleport);
            sender.teleport(dataStore.getLocation(sender.getUniqueId().toString(), teleport));

        } else {
            throw new Exception("Can't switch to same world");
        }

    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(s.equals("switch")){
            if(strings.length == 0){
                listWorlds(commandSender);
                return true;

            } else if(strings.length == 1){
                try {
                    handleSwitch(commandSender, strings);
                    return true;
                } catch (Exception e) {
                    commandSender.sendMessage(e.getMessage());
                }

            } else if(strings.length == 2){
                try {
                    handleOpSwitch(strings);
                    return true;
                } catch(Exception e){
                    commandSender.sendMessage(e.getMessage());
                }

            }
        }

        return false;
    }
}
