package com.xessmcserver.serverlocator.util.challenges;

import com.xessmcserver.serverlocator.util.Challenge;
import com.xessmcserver.serverlocator.util.ChallengeTier;
import org.bukkit.entity.Player;

public class ChallengeNone extends Challenge {

    public ChallengeNone(Player challenger, ChallengeTier tier)
    {
        super(challenger, "None", tier,0);
    }

    @Override
    public boolean onChallengeStart() {
        challenger.sendMessage("Teleporting home!");
        return false;
    }

    @Override
    public boolean challenge() {
        return true;
    }

    @Override
    public boolean onChallengeComplete() {
        challenger.sendMessage("Welcome home!");
        return true;
    }
}
