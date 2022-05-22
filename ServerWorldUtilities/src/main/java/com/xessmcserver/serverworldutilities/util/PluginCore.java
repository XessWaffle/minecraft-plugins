package com.xessmcserver.serverworldutilities.util;

import com.xessmcserver.serverworldutilities.cmd.*;
import com.xessmcserver.serverworldutilities.io.PlayerData;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginCore extends JavaPlugin{

    private WorldList worlds;

    private CommandSwitch cmdSwitch;
    private CommandCreate cmdCreate;
    private CommandBackup cmdBackup;
    private CommandDelete cmdDelete;

    private PlayerData playerData;

    @Override
    public void onEnable() {
        super.onEnable();

        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        worlds = new WorldList(this);

        playerData = new PlayerData(worlds,this);

        cmdSwitch = new CommandSwitch(worlds);
        cmdSwitch.ref = this;
        cmdSwitch.dataStore = playerData;

        cmdCreate = new CommandCreate(worlds);
        cmdCreate.ref = this;
        cmdCreate.dataStore = playerData;

        cmdBackup = new CommandBackup(worlds);
        cmdBackup.ref = this;

        cmdDelete = new CommandDelete(worlds);
        cmdDelete.ref = this;
        cmdDelete.dataStore = playerData;

        this.getCommand("switch").setExecutor(cmdSwitch);
        this.getCommand("create").setExecutor(cmdCreate);
        this.getCommand("backup").setExecutor(cmdBackup);
        this.getCommand("delete").setExecutor(cmdDelete);
        this.getCommand("current").setExecutor(new CommandCurrent());

        this.getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }


    class EventListener implements Listener{
        @EventHandler
        public void onJoin(PlayerJoinEvent event){

            if(playerData.hasPlayer(event.getPlayer().getUniqueId().toString())) {
                Location prev = playerData.getLocation(event.getPlayer().getUniqueId().toString(), event.getPlayer().getWorld());
                event.getPlayer().teleport(prev);
            } else {
                playerData.refresh();
            }
        }

        @EventHandler
        public void onDisconnect(PlayerQuitEvent event){
            if(playerData.hasPlayer(event.getPlayer().getUniqueId().toString())) {
                playerData.setLocation(event.getPlayer().getUniqueId().toString(), event.getPlayer().getWorld().getName(), event.getPlayer().getLocation());
            } else {
                playerData.refresh();
            }
        }

        @EventHandler
        public void onRespawn(PlayerRespawnEvent event){
            if(playerData.hasPlayer(event.getPlayer().getUniqueId().toString())) {
                playerData.invokeRespawn(event);
            } else {
                playerData.refresh();
            }


        }

        @EventHandler
        public void onBedClick(PlayerBedEnterEvent event){
            if(playerData.hasPlayer(event.getPlayer().getUniqueId().toString())) {
                playerData.setRespawn(event.getPlayer(), event.getPlayer().getLocation());
            } else {
                playerData.refresh();
            }
        }
    }

}

