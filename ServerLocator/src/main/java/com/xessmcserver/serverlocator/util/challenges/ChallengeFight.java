package com.xessmcserver.serverlocator.util.challenges;

import com.xessmcserver.serverlocator.util.Challenge;
import com.xessmcserver.serverlocator.util.ChallengeTier;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import java.util.ArrayList;

public class ChallengeFight extends Challenge {
    /*
    Player needs to fight off a wave of mobs before being teleported
     */
    private ArrayList<Zombie> toKill;

    public ChallengeFight(Player challenger, ChallengeTier tier) {
        super(challenger, "Fight", tier, 5);
        toKill = new ArrayList<>();
    }

    @Override
    public boolean onChallengeStart() throws Exception {
        ChallengeTier tier = getTier();
        switch (tier){
            case BITCH:
            case WHY:
            case HARD:
                for(int i = 0; i < 10; i++) {
                    Zombie zombie = (Zombie) challenger.getWorld().spawnEntity(challenger.getLocation().add(((Math.random() - 0.5) * 10), 0, ((Math.random() - 0.5) * 10)), EntityType.ZOMBIE);
                    zombie.setCustomName("Bob " + i);
                    toKill.add(zombie);
                }
                break;
            case MEDIUM:
            case EASY:
                for(int i = 0; i < 3; i++) {
                    Zombie zombie = (Zombie) challenger.getWorld().spawnEntity(challenger.getLocation().add(((Math.random() - 0.5) * 10), 0, ((Math.random() - 0.5) * 10)), EntityType.ZOMBIE);
                    zombie.setCustomName("Bob " + i);
                    toKill.add(zombie);
                }
                break;
        }
        challenger.sendMessage("Kill Bob");
        return false;
    }

    @Override
    public boolean challenge() throws Exception {

        boolean complete = true;

        for(Zombie z: toKill)
        {
            if(!z.isDead())
                complete = false;
        }

        return complete;
    }

    @Override
    public boolean onChallengeComplete() throws Exception {
        String[] congratulatoryMessages = {
                "You aced it! Your Minecraft skills are legendary!",
                "Incredible work! You just set the bar higher.",
                "Hats off! You've nailed this challenge like a pro.",
                "Amazing job! You've conquered that challenge.",
                "You did it! Your dedication paid off.",
                "Impressive! You've outdone yourself again.",
                "Challenge complete! You're unstoppable.",
                "You smashed it! On to the next adventure.",
                "Bravo! You've proven your prowess.",
                "Phenomenal effort! You've reached new heights.",
                "That wasn't so bad was it!"
        };


        challenger.sendMessage(congratulatoryMessages[(int) (Math.random() * congratulatoryMessages.length)]);
        return true;
    }
}
