package com.xessmcserver.serverlocator.util.challenges;

import com.xessmcserver.serverlocator.util.Challenge;
import com.xessmcserver.serverlocator.util.ChallengeTier;
import com.xessmcserver.serverlocator.util.runnables.DropRunnable;
import com.xessmcserver.serverlocator.util.runnables.TeleporterRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ChallengeConfusion extends Challenge {

    private static final int MAX_RADIAL_LOCATION_DELTA = 0;
    private long challengeStart;

    private int currentDelta, prevDelta;

    private float teleportProb, dropProb;
    private int instanceDiv;

    public ChallengeConfusion(Player challenger, ChallengeTier tier)
    {
        super(challenger, "Confusion", tier, 5);
    }

    @Override
    public boolean onChallengeStart() {
        challenger.sendMessage("Deal with this first");
        challengeStart = System.currentTimeMillis();

        ChallengeTier tier = getTier();
        switch (tier){
            case BITCH:
            case WHY:
            case HARD:
                challenger.setWalkSpeed(0.8f);
                teleportProb = 0.5f;
                dropProb = 0.1f;
                instanceDiv = 100;
                break;
            case MEDIUM:
            case EASY:
                challenger.setWalkSpeed(0.4f);
                teleportProb = 0.25f;
                dropProb = 0.01f;
                instanceDiv = 250;
                break;
        }
        return false;
    }

    @Override
    public boolean challenge() {

        prevDelta = currentDelta;
        currentDelta = (int)((System.currentTimeMillis() - challengeStart) / instanceDiv);

        if(Math.random() < teleportProb && currentDelta != prevDelta) {
            Location current = challenger.getLocation();

            double newX = current.getX() + (Math.random() - 0.5) * MAX_RADIAL_LOCATION_DELTA,
                    newZ = current.getZ() + (Math.random() - 0.5) * MAX_RADIAL_LOCATION_DELTA,
                    newY = current.getY()/* challenger.getWorld().getHighestBlockYAt((int)newX, (int)newZ) */;

            Location random = new Location(challenger.getWorld(), newX, newY, newZ);

            random.setPitch((float)(current.getPitch() + (Math.random() - 0.5) * 5));
            random.setYaw((float)(current.getYaw() + (Math.random()) * 10));

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ref, new TeleporterRunnable(challenger, random));
        }

        if(Math.random() < dropProb && currentDelta != prevDelta) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ref, new DropRunnable(challenger));
        }

        return (System.currentTimeMillis() - challengeStart) > 10000;
    }

    @Override
    public boolean onChallengeComplete() {
        challenger.setWalkSpeed(0.2f);
        return true;
    }
}
