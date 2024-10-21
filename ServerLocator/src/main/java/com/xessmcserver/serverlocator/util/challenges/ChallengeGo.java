package com.xessmcserver.serverlocator.util.challenges;

import com.xessmcserver.serverlocator.util.Challenge;
import com.xessmcserver.serverlocator.util.ChallengeTier;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ChallengeGo extends Challenge {

    private Location startPlayerLocation;
    private int radialDelta, verticalDelta;

    public ChallengeGo(Player challenger, ChallengeTier tier) {
        super(challenger, "Go", tier, 5);
    }

    @Override
    public boolean onChallengeStart() throws Exception {
        startPlayerLocation = challenger.getLocation();

        switch (getTier()) {
            case WHY:
            case BITCH:
            case HARD:
                radialDelta = (int) ((Math.random()) * 50);
                verticalDelta = (int) (Math.random() - 0.5) * 20;
                break;
            case MEDIUM:
                radialDelta = (int) ((Math.random()) * 20);
                verticalDelta = (int) (Math.random() * 20);
                break;
            case EASY:
                radialDelta = 0;
                verticalDelta = (int) (Math.random() * 10);
                break;
        }

        if(verticalDelta < 0) {
            challenger.sendMessage("You must move at least " + radialDelta + " blocks away and " + Math.abs(verticalDelta) + " blocks down from where you are now");
        } else if (verticalDelta == 0) {
            challenger.sendMessage("You must move at least " + radialDelta + " blocks away from where you are now");
        } else {
            challenger.sendMessage("You must move at least " + radialDelta + " blocks away and " + Math.abs(verticalDelta) + " blocks up from where you are now");
        }

        return false;
    }

    @Override
    public boolean challenge() throws Exception {

        Location currentLocation = challenger.getLocation();

        double distance = currentLocation.distance(startPlayerLocation);
        int vertical = currentLocation.getBlockY() - startPlayerLocation.getBlockY();

        return distance > radialDelta && Math.abs(vertical) > Math.abs(verticalDelta);
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
