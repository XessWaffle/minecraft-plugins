package com.xessmcserver.serverworldutilities.cmd;

import com.xessmcserver.serverworldutilities.io.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.xessmcserver.serverworldutilities.util.PluginCore;
import com.xessmcserver.serverworldutilities.util.WorldList;


import java.io.File;
import java.util.HashMap;

public class CommandDelete implements CommandExecutor {

    private WorldList worlds;
    private HashMap<String, Boolean> confirmation;

    private File toDelete;
    private String worldName;

    public PluginCore ref;
    public PlayerData dataStore;

    public CommandDelete(WorldList worlds){
        this.worlds = worlds;
        this.confirmation = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(s.equals("delete")){
            if(strings.length == 1){
                Player sender = Bukkit.getPlayer(commandSender.getName());

                if(strings[0].equals("world") || strings[0].equals("world_nether") || strings[0].equals("world_the_end") || strings[0].equals(sender.getWorld().getName())){
                    commandSender.sendMessage("Illegal to delete this world!");
                    return true;
                } else {
                    if(!confirmation.containsKey(sender.getUniqueId().toString())) {
                        if (worlds.hasWorld(strings[0])) {
                            toDelete = new File(ref.getServer().getWorldContainer().getAbsolutePath() + "/" + strings[0]);
                            worldName = strings[0];
                            confirmation.put(sender.getUniqueId().toString(), true);

                        } else {
                            commandSender.sendMessage("World " + strings[0] +  " Not Found");
                        }

                        return true;
                    } else if(confirmation.get(sender.getUniqueId().toString())){
                        if(strings[0].equals("confirm")) {

                            Bukkit.unloadWorld(worldName, false);

                            commandSender.sendMessage("Deleting...");
                            deleteDirectory(toDelete);
                            worlds.removeWorld(worldName);
                            dataStore.removeWorld(worldName);

                            toDelete = null;
                            worldName = "";

                            commandSender.sendMessage("Deleted!");

                        } else {
                            commandSender.sendMessage("Failed to Delete");
                            toDelete = null;

                        }

                        confirmation.remove(sender.getUniqueId().toString());

                        return true;
                    }
                }

            }
        }


        return false;
    }

    private boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}


