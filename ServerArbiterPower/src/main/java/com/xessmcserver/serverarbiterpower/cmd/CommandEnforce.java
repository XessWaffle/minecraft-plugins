package com.xessmcserver.serverarbiterpower.cmd;

import com.xessmcserver.serverarbiterpower.io.DecreeManager;
import com.xessmcserver.serverarbiterpower.util.Decree;
import com.xessmcserver.serverarbiterpower.util.DropItem;
import com.xessmcserver.serverarbiterpower.util.Explosion;
import com.xessmcserver.serverarbiterpower.util.Teleport;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandEnforce implements CommandExecutor{

    private DecreeManager ref;

    public CommandEnforce(DecreeManager reference) {
        this.ref = reference;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // TODO Auto-generated method stub
        try {
            if (cmd.getName().equals("enforce")) {
                handleEnforce(args);
            } else if (cmd.getName().equals("enforced")) {
                handleEnforced(sender);
            } else if (cmd.getName().equals("unenforce")) {
                handleUnenforce(args);
            }
        } catch (Exception e){
           sender.sendMessage(e.getMessage());
        }

        return true;

    }

    private void handleUnenforce(String[] args) throws Exception{

        Player toUnenforce = Bukkit.getPlayer(args[0]);
        if(toUnenforce == null){
            throw new Exception("Player not currently online");
        }

        if(args.length == 1){
            ref.unenforcePlayer(args[0]);
        } else if(args.length == 2){
            ref.removeDecree(args[0], args[1]);
        }

    }

    private void handleEnforced(CommandSender sender) throws Exception{

        String enforced = "Enforced: ";

        for(String player: ref.players()){
           enforced += player;
           enforced += ", ";
        }

        sender.sendMessage(enforced);
    }

    private void handleEnforce(String[] args) throws Exception{
        Player toEnforce = Bukkit.getPlayer(args[0]);
        if(toEnforce == null){
            throw new Exception("Player not currently online");
        }

        if(args.length < 3){
            throw new Exception("Enter <name> <decree> <probability>");
        }

        Decree toEnqueue = null;

        if(args[1].equals("Drop")){
            toEnqueue = new DropItem(toEnforce);
        } else if(args[1].equals("Explosion")){
            toEnqueue = new Explosion(toEnforce);
        } else if(args[1].equals("Teleport")){
            toEnqueue = new Teleport(toEnforce);
        } else if(args[1].equals("Lightning")){
            toEnqueue = new Teleport(toEnforce);
        }

        if(toEnqueue != null) {
            toEnqueue.setProbability(Float.parseFloat(args[2]));
        }

        ref.addDecree(args[0], toEnqueue);
    }

}
