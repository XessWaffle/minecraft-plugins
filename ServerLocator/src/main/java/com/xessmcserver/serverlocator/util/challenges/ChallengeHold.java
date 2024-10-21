package com.xessmcserver.serverlocator.util.challenges;

import com.xessmcserver.serverlocator.util.Challenge;
import com.xessmcserver.serverlocator.util.ChallengeTier;
import org.bukkit.entity.Player;

public class ChallengeHold extends Challenge {

    /*
    Player needs to find and hold a certain item
     */
    public ChallengeHold(Player challenger, ChallengeTier tier) {
        super(challenger, "Hold", tier, 5);
    }

    @Override
    public boolean onChallengeStart() throws Exception {
        return false;
    }

    @Override
    public boolean challenge() throws Exception {
        return false;
    }

    @Override
    public boolean onChallengeComplete() throws Exception {
        return false;
    }
}
