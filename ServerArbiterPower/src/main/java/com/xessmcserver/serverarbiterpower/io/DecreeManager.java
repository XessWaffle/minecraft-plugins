package com.xessmcserver.serverarbiterpower.io;

import com.xessmcserver.serverarbiterpower.util.Decree;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

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
            decrees.get(player).clear();
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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        if(enabled) {
            String playerName = event.getPlayer().getName();
            Queue<Decree> playerDecrees = decrees.get(playerName);
            if(playerDecrees == null){
                return;
            }

            Decree toEnforce = playerDecrees.poll();
            
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

}
