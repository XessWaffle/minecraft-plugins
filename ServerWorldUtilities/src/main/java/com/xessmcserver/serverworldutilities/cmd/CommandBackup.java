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
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandBackup implements CommandExecutor{

    private WorldList worlds;
    public ServerWorldUtilities ref;
    public HashMap<String, Boolean> worldBackupService;
    public CommandBackup(WorldList worlds){
        this.worlds = worlds;
        worldBackupService = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(s.equals("backup")){

            commandSender.sendMessage("Initiating Backup...");

            Player sender = Bukkit.getPlayer(commandSender.getName());

            String worldPath = ref.getServer().getWorldContainer().getAbsolutePath();
            String worldName = sender.getWorld().getName();
            String dataPath = ref.getDataFolder().getAbsolutePath();

            worldBackupService.putIfAbsent(worldName, false);

            if(!worldBackupService.get(worldName)) {
                ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
                final Runnable backupService = new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage("Backup Initiated");
                        backup(dataPath, worldPath, worldName, true);
                        Bukkit.getServer().broadcastMessage("Backup Complete!");
                    }
                };

                ses.scheduleAtFixedRate(backupService, 5, 5, TimeUnit.HOURS);

                worldBackupService.put(worldName, true);
                commandSender.sendMessage("Backup Service Started!");
            }

            backup(dataPath, worldPath, worldName, false);

            commandSender.sendMessage("Backup Complete!");
            return true;
        }

        return false;
    }

    public static void backup(String dataPath, String worldPath, String worldName, boolean auto)
    {
        File from = new File(worldPath + "/" + worldName);
        File to;

        if(auto) {
            to = new File(dataPath + "/autobackup-" + worldName + "-" + System.currentTimeMillis());
        } else {
            to = new File(dataPath + "/backup-" + worldName + "-" + System.currentTimeMillis());
        }
        try {
            copyDirectory(from.getAbsolutePath(), to.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
