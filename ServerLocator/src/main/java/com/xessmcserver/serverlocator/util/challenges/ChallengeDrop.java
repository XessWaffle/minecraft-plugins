package com.xessmcserver.serverlocator.util.challenges;

import com.xessmcserver.serverlocator.util.Challenge;
import com.xessmcserver.serverlocator.util.ChallengeTier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ChallengeDrop extends Challenge {

    private int startingItemCount, toDrop, latestItemCount;
    private long challengeStart;

    private boolean succeeded = false;

    public ChallengeDrop(Player challenger, ChallengeTier tier) {
        super(challenger, "Drop", tier, 5);
        startingItemCount = 0;
    }

    private int getItemCount()
    {
        PlayerInventory inventory = challenger.getInventory();
        ItemStack[] items = inventory.getContents();
        int has = 0;
        for (ItemStack item : items)
        {
            if ((item != null) && (item.getAmount() > 0))
            {
                has += item.getAmount();
            }
        }
        return has;
    }
    @Override
    public boolean onChallengeStart() throws Exception {
        ChallengeTier tier = getTier();
        startingItemCount = getItemCount();
        switch (tier) {
            case WHY:
            case BITCH:
                toDrop = (int)((Math.random()) * startingItemCount);
            case HARD:
                toDrop = (int)((Math.random() / 3.0) * startingItemCount);
                break;
            case MEDIUM:
            case EASY:
                toDrop = 1;
                break;
        }
        challenger.sendMessage("You have 15 seconds to drop exactly " + toDrop + " item(s) from your inventory");
        challengeStart = System.currentTimeMillis();
        return false;
    }

    @Override
    public boolean challenge() throws Exception {
        latestItemCount = getItemCount();

        if(startingItemCount - toDrop == latestItemCount)
            succeeded = true;
        else
            succeeded = false;

        return System.currentTimeMillis() - challengeStart > 10000;
    }

    @Override
    public boolean onChallengeComplete() throws Exception {
        challenger.sendMessage("You started with " + startingItemCount + " items and ended with " + latestItemCount + " items");
        if(succeeded) {
            challenger.sendMessage("Congratulations, you know how to count!");
            return true;
        } else {
            challenger.sendMessage("Should've paid more attention in math, try again next time!");
            return false;
        }

    }
}
