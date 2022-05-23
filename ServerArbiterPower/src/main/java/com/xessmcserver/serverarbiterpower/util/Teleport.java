package com.xessmcserver.serverarbiterpower.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;


public class Teleport extends Decree{

    public static final int DEVIATION = 2;

    public Teleport(Player enforced) {
        super(enforced, "Teleport", 0.01, false);
    }

    @Override
    public void enforce() {

        Location current = enforced.getLocation();

        current.add((Math.random() - 0.5) * 2 * DEVIATION,
                (Math.random() - 0.5) * 2 * DEVIATION,
                (Math.random() - 0.5) * 2 * DEVIATION);

        enforced.teleport(current);

    }
}
