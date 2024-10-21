package com.xessmcserver.serverlocator.util.challenges;

import com.xessmcserver.serverlocator.util.Challenge;
import com.xessmcserver.serverlocator.util.ChallengeTier;
import org.bukkit.entity.Player;

public class ChallengeHit extends Challenge {
    /*
    Player needs to hit a mob or another player being teleported
     */
    public ChallengeHit(Player challenger, ChallengeTier tier) {
        super(challenger, "Hit", tier, 5);
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
