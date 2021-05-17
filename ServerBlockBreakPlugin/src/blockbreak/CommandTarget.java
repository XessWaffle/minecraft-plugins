package blockbreak;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTarget implements CommandExecutor{
	
	public HashSet<String> targets;
	
	public CommandTarget() {
		targets = new HashSet<>();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		if(cmd.getName().equals("target")) {
			if(args.length == 1) {
				Player target = Bukkit.getPlayerExact(args[0]);
				
				if(target != null) {
					targets.add(target.getName());
				}
				
			} else {
				sender.sendMessage("Incorrect Number of Arguments");
				return false;
			}
		} else if(cmd.getName().equals("untarget")) {
			if(args.length == 1) {
				Player target = Bukkit.getPlayerExact(args[0]);
				
				if(target != null) {
					targets.remove(target.getName());
				}
				
			} else {
				sender.sendMessage("Incorrect Number of Arguments");
				return false;
			}
		} else {
			for(String target: targets) {
				sender.sendMessage(target);
			}
		}
			
	        // If the player (or console) uses our command correct, we can return true
        return true;
	}
	
	public HashSet<String> getTargets(){
		return targets;
	}

}
