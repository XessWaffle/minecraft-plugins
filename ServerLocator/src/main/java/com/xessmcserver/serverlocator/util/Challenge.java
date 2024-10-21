package com.xessmcserver.serverlocator.util;

import com.xessmcserver.serverlocator.ServerLocator;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Challenge {

    protected Player challenger;
    protected String challengeName;
    protected long delay;

    protected static ServerLocator ref;

    private Location teleportDestination;

    private final ChallengeTier tier;

    public Challenge(Player challenger, String name, ChallengeTier tier, long delay)
    {
        this.challenger = challenger;
        this.challengeName = name;
        this.delay = delay;
        this.tier = tier;

        this.teleportDestination = challenger.getBedSpawnLocation();

        if(this.teleportDestination == null) {
            this.teleportDestination = challenger.getWorld().getSpawnLocation();
        }
    }


    public Player getChallenger() {
        return challenger;
    }

    public ChallengeTier getTier() {
        return tier;
    }

    public String getChallengeName(){
        return challengeName;
    }

    public Location getTeleportDestination() {
        return teleportDestination;
    }

    public void setTeleportDestination(Location teleportDestination) {
        this.teleportDestination = teleportDestination;
    }

    public long getDelay() {
        return delay;
    }
    public abstract boolean onChallengeStart() throws Exception;

    public abstract boolean challenge() throws Exception;

    public abstract boolean onChallengeComplete() throws Exception;

}
