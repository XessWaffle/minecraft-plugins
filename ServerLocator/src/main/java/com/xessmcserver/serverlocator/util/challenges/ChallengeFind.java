package com.xessmcserver.serverlocator.util.challenges;

import com.xessmcserver.serverlocator.util.Challenge;
import com.xessmcserver.serverlocator.util.ChallengeTier;
import org.bukkit.entity.Player;

public class ChallengeFind extends Challenge {

    /*
    Player needs to search for a block that has been spawned within a radius and find it before being teleported back
     */
    public ChallengeFind(Player challenger, ChallengeTier tier) {
        super(challenger, "Find", tier, 5);
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
