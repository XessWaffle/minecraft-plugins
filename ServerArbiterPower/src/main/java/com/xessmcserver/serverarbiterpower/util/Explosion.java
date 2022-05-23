package com.xessmcserver.serverarbiterpower.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Explosion extends Decree {

    public Explosion(Player enforced) {
        super(enforced, "Explosion", 0.01, false);
    }

    @Override
    public void enforce() {
        Location loc = enforced.getLocation();
        World world = enforced.getWorld();

        world.createExplosion(loc, 0.0f);
    }

}
