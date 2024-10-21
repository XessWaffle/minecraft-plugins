package com.xessmcserver.serverlocator.util;

import com.xessmcserver.serverlocator.util.challenges.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class ChallengeGenerator {

    public static HashMap<ChallengeTier, ArrayList<ChallengeType>> registeredChallenges;

    public static void registerChallenge(ChallengeType name, ChallengeTier ... tiers){
        if(registeredChallenges == null)
            registeredChallenges = new HashMap<>();

        for(ChallengeTier tier: tiers) {
            registeredChallenges.putIfAbsent(tier, new ArrayList<>());
            ArrayList<ChallengeType> types = registeredChallenges.get(tier);
            types.add(name);
        }
    }

    public static Challenge pickChallenge(Player challenger, ChallengeTier tier) {
        int numChallenges = registeredChallenges.get(tier).size();
        ChallengeType randomChallenge = registeredChallenges.get(tier).get((int)(Math.random() * numChallenges));

        switch (randomChallenge){
            case CONFUSION:
                return new ChallengeConfusion(challenger, tier);
            case DROP:
                return new ChallengeDrop(challenger, tier);
            case GO:
                return new ChallengeGo(challenger, tier);
            case FIGHT:
                return new ChallengeFight(challenger, tier);
            case FIND:
                return new ChallengeFind(challenger, tier);
            case HOLD:
                return new ChallengeHold(challenger, tier);
            case HIT:
                return new ChallengeHit(challenger, tier);
            case NONE:
                return new ChallengeNone(challenger, tier);
        }

        return new ChallengeError(challenger, tier);

    }
}
