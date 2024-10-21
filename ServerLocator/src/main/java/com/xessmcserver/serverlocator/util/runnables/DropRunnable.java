package com.xessmcserver.serverlocator.util.runnables;

import org.bukkit.entity.Player;

public class DropRunnable implements Runnable{

    private Player player;

    public DropRunnable(Player player){
        this.player = player;
    }

    @Override
    public void run() {
        player.dropItem(false);
    }
}
