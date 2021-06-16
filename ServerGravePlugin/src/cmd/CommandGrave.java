package cmd;

import io.GraveData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import utils.Grave;

public class CommandGrave implements CommandExecutor {

    private GraveData graveData;

    public CommandGrave(GraveData graveData){
        this.graveData = graveData;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(s.equals("grave")){

            Player sender = Bukkit.getPlayer(commandSender.getName());



            if(strings.length == 1){
                if (strings[0].equals("list")){

                    int i = 0;

                    for(Location l: graveData.getGraves(sender)) {
                        commandSender.sendMessage("Grave " + (i++) + ": " + "(" + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ() + ") in " + l.getWorld().getName());
                    }
                } else if(strings[0].equals("prune")){
                    if(sender.isOp()) {
                        graveData.prune(commandSender);
                    }
                }

                return true;
            } else if(strings.length == 3){
                if(strings[0].equals("restore") && sender.isOp()){
                    int i = 0;

                    for(Location l: graveData.getGraves(Bukkit.getPlayer(strings[1]))){

                        if(i == Integer.parseInt(strings[2])){
                            Grave restored = graveData.getGraveAtLocation(Bukkit.getPlayer(strings[1]), l);
                            restored.dropItems(sender.getLocation());

                            commandSender.sendMessage("Grave " + (i) + ": " + "(" + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ() + ") in " + l.getWorld().getName() + " restored!");
                            graveData.removeGrave(Bukkit.getPlayer(strings[1]), restored);
                        }

                        i++;


                    }
                }

                return true;
            }
        }

        return false;
    }
}
