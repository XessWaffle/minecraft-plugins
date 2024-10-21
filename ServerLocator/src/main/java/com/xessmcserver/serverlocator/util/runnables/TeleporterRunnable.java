package com.xessmcserver.serverlocator.util.runnables;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleporterRunnable implements Runnable {
    private final Player player;
    private final Location location;

    public TeleporterRunnable(Player player, Location location) {
        this.player = player;
        this.location = location;
    }

    @Override
    public void run() {
        if (location != null) {
            player.teleport(location);
        }
    }
}