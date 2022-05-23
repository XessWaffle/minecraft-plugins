package com.xessmcserver.serverarbiterpower.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Timer;
import java.util.TimerTask;

public class Teleport extends Decree{

    public Teleport(Player enforced) {
        super(enforced, "Teleport", 0.0, true);
    }

    @Override
    public void enforce() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "switch " + enforced.getName() + " dungeon");

        class TimerTaskHelper extends TimerTask {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "switch " + enforced.getName() + " world");
            }
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTaskHelper(), 3000);

    }
}
