package com.xessmcserver.serverarbiterpower.util;

import org.bukkit.entity.Player;

public class Lightning extends Decree{

    public Lightning(Player enforced) {
        super(enforced, "Lightning", 0.01, false);
    }

    @Override
    public void enforce() {
        enforced.getWorld().strikeLightningEffect(enforced.getLocation());
    }
}
