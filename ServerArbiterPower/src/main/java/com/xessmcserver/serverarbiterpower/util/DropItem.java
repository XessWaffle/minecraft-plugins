package com.xessmcserver.serverarbiterpower.util;

import org.bukkit.entity.Player;

public class DropItem extends Decree{

    public DropItem(Player enforced) {
        super(enforced, "Drop", 0.01, false);
    }

    @Override
    public void enforce() {
        enforced.dropItem(false);
    }
}
