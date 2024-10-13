package com.xessmcserver.serverarbiterpower.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Jail extends Decree{

    public static final int DEVIATION = 2;

    private Location jail;

    public Jail(Player enforced) {
        super(enforced, "Jail", 1, false);
        jail = null;
    }

    @Override
    public void enforce() {

        Location current = enforced.getLocation();
        if (jail == null)
            jail = current;

        if(Math.abs(current.getX() - jail.getX()) > DEVIATION
                || Math.abs(current.getY() - jail.getY()) > DEVIATION
                || Math.abs(current.getZ() - jail.getZ()) > DEVIATION)
            enforced.teleport(jail);

    }
}
