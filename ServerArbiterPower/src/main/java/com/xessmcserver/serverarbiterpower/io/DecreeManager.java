package com.xessmcserver.serverarbiterpower.io;

import com.xessmcserver.serverarbiterpower.util.Decree;

import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class DecreeManager extends TimerTask {

    private boolean enabled;

    private HashMap<String, Queue<Decree>> decrees;
    private HashMap<String, String> remove;

    public DecreeManager(){
        decrees = new HashMap<>();
        remove = new HashMap<>();

        Timer timer = new Timer();
        timer.schedule(this, 1000, 2500);

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

    @Override
    public void run(){
        if(enabled) {
            for(String playerName: this.players()) {
                Queue<Decree> playerDecrees = decrees.get(playerName);
                if (playerDecrees == null) {
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

}
