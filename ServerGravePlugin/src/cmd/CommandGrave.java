package cmd;

import io.GraveData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGrave implements CommandExecutor {

    private GraveData graveData;

    public CommandGrave(GraveData graveData){
        this.graveData = graveData;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(s.equals("grave")){
            if(strings.length == 1){

                Player sender = Bukkit.getPlayer(commandSender.getName());

                if(strings[0].equals("list")){

                    int i = 0;

                    for(Location l: graveData.getGraves(sender)){
                        commandSender.sendMessage("Grave " + (i++) + ": " + "(" + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ() + ") in " + l.getWorld().getName());
                    }
                }

                return true;
            }
        }

        return false;
    }
}
