package com.xessmcserver.serverarbiterpower.io;

import com.xessmcserver.serverarbiterpower.util.Decree;
import com.xessmcserver.serverarbiterpower.util.Jail;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;


import java.util.*;

public class DecreeManager implements Listener {

    private boolean enabled;

    private HashMap<String, Queue<Decree>> decrees;
    private HashMap<String, String> remove;

    public DecreeManager(){
        decrees = new HashMap<>();
        remove = new HashMap<>();

    }

    public void addDecree(String player, Decree decree){

        if(decree == null){
            return;
        }

        if(!decrees.containsKey(player)){
            Queue<Decree> decreeQueue = new LinkedList<>();
            decreeQueue.add(decree);
            decrees.put(player, decreeQueue);
        } else {
            decrees.get(player).add(decree);
        }
    }

    public void removeDecree(String player, String name){
        remove.putIfAbsent(player, name);
    }

    public void unenforcePlayer(String player){
        if(decrees.containsKey(player)){
            decrees.remove(player);
        }
    }

    public HashSet<String> players(){
        return new HashSet<>(decrees.keySet());
    }

    public void enable(){
        enabled = true;
    }

    public void disable(){
        enabled = false;
    }

    public void processDecree(String playerName) {
        if (enabled) {
            Queue<Decree> playerDecrees = decrees.get(playerName);
            if (playerDecrees == null) {
                return;
            }
            Decree toEnforce = playerDecrees.poll();

            if(toEnforce == null)
            {
                return;
            }

            if (toEnforce.isSingleTimeEvent()) {
                toEnforce.enforce();
            } else if (Math.random() < toEnforce.getProbability()) {
                toEnforce.enforce();
            }

            if (remove.containsKey(playerName) && toEnforce.getName().equals(remove.get(playerName))) {
                remove.put(playerName, "");

            } else {
                playerDecrees.add(toEnforce);
            }

        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        processDecree(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        if(enabled) {

            Jail jailDecree = new Jail(event.getPlayer());

            HashSet<String> playerNames = new HashSet<>();

            for (OfflinePlayer offlinePlayer : Bukkit.getServer().getWhitelistedPlayers()){
                playerNames.add(offlinePlayer.getName());
            }

            if(!playerNames.contains(event.getPlayer().getName())) {
                addDecree(event.getPlayer().getName(), jailDecree);
            } else {
                removeDecree(event.getPlayer().getName(), jailDecree.getName());
            }
        }
    }

}
