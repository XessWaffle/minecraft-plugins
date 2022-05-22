package com.xessmcserver.serverworldutilities.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.xessmcserver.serverworldutilities.ServerWorldUtilities;
import com.xessmcserver.serverworldutilities.util.WorldList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommandBackup implements CommandExecutor{

    private WorldList worlds;

    public ServerWorldUtilities ref;

    public CommandBackup(WorldList worlds){
        this.worlds = worlds;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(s.equals("backup")){

            commandSender.sendMessage("Initiating Backup...");

            Player sender = Bukkit.getPlayer(commandSender.getName());

            File from = new File(ref.getServer().getWorldContainer().getAbsolutePath() + "/" + sender.getWorld().getName());
            File to = null;

            if(strings.length == 0) {
                to = new File(ref.getDataFolder() + "/backup-" + sender.getWorld().getName());
            } else {
                to = new File(ref.getDataFolder() + "/backup-" + sender.getWorld().getName() + "-" + strings[0]);
            }

            try {
                copyDirectory(from.getAbsolutePath(), to.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            commandSender.sendMessage("Backup Complete!");
            return true;
        }

        return false;
    }

    public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation)
            throws IOException {
        Files.walk(Paths.get(sourceDirectoryLocation))
                .forEach(source -> {
                    Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                            .substring(sourceDirectoryLocation.length()));
                    try {
                        if(!source.toFile().getName().equals("session.lock")) {
                            Files.copy(source, destination);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

}
