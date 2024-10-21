package com.xessmcserver.serverlocator.cmd;

import com.xessmcserver.serverlocator.ServerLocator;
import com.xessmcserver.serverlocator.util.ChallengeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHome implements CommandExecutor {

    private static ChallengeManager challengeManager;
    private static ServerLocator ref;
    public CommandHome(ServerLocator ref){
        challengeManager = new ChallengeManager(ref);
        this.ref = ref;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player challenger = Bukkit.getPlayer(commandSender.getName());
        if(s.equals("home")) {
            boolean challengeStarted = challengeManager.startChallenge(challenger);
            if(challengeStarted)
                challenger.sendMessage("Selecting a challenge...");
        } else if(s.equals("sethome")) {
            challengeManager.setHomeLocation(challenger.getName(), challenger.getLocation());
            challenger.sendMessage("Set new home at (" + challenger.getLocation().getBlockX() + ", " + challenger.getLocation().getBlockY() + ", " + challenger.getLocation().getBlockZ() + ")");
        }




        // Determine challenge/forfeit based on location
        /*
            1. Add player to queue to receive challenge (info challenger)
            2. Separate Executor thread to manage that queue
         */
        /*
            Challenge Abstract Class
            1. Player and Name to instantiate
            2. Pre-challenge (run once, explains challenge to player)
            3. Challenge (run once, instantiates the challenge)
         */


        // Activate thread to monitor challenge status loop

        // Once complete teleport user back to world spawn


        return true;
    }
}
