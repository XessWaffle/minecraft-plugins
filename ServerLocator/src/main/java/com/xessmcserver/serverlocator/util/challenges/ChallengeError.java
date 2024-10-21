package com.xessmcserver.serverlocator.util.challenges;

import com.xessmcserver.serverlocator.util.Challenge;
import com.xessmcserver.serverlocator.util.ChallengeTier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChallengeError extends Challenge {

    public ChallengeError(Player challenger, ChallengeTier tier)
    {
        super(challenger, "Error", tier, 0);
    }

    @Override
    public boolean onChallengeStart() throws Exception {
        Bukkit.getServer().broadcastMessage("We shouldn't have gotten here, you broke the code " + challenger.getName());
        return false;
    }

    @Override
    public boolean challenge() throws Exception {
        return true;
    }

    @Override
    public boolean onChallengeComplete() throws Exception {
        return false;
    }
}
